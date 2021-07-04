package com.nitroex.my_memo.ui

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.nitroex.my_memo.BaseActivity
import com.nitroex.memo.my_memo.R
import com.nitroex.my_memo.service.APICode
import com.nitroex.my_memo.service.RetrofitClient
import com.nitroex.my_memo.ui.memo_status.MemoStatusActivity
import com.nitroex.my_memo.utils.adapter.ProfileAdapter
import com.nitroex.my_memo.utils.model.CMModel
import com.nitroex.my_memo.utils.model.CMProfile
import com.nitroex.my_memo.utils.model.CurrentDocument
import com.nitroex.my_memo.utils.Configs
import com.nitroex.my_memo.utils.dialog.DialogSignature
import com.nitroex.my_memo.utils.listener.CommonResponseListener
import com.nitroex.my_memo.utils.listener.OnDialogCallbackListener
import com.nitroex.my_memo.utils.listener.OnDialogDismissListener
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.view_icon_bottom.*
import kotlinx.android.synthetic.main.view_title_head.*
import org.json.JSONObject

class ProfileActivity : BaseActivity(), CommonResponseListener, ProfileAdapter.OnClickListListener, DialogSignature.OnInputListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setClick()
        setView()
        getUserProfile()
    }

    private fun setClick() {
        btnHeadBack.setOnClickListener { onBackPressed() }
        btnFabIcon.setOnClickListener { openActivityFinish(MainActivity::class.java)}
        btnImportSignature.setOnClickListener {
            DialogSignature.display(supportFragmentManager, object : OnDialogCallbackListener {
                override fun onSubmit() {}
                override fun onCancel() {}
            }, false)
        }
    }

    private fun setView() {
        val bundle = getBundleFromIntent(this)
        if (bundle != null) {
            setTitleAppBar(bundle)
        }
    }

    private fun getUserProfile() {
        RetrofitClient().newInstance()
            .setNetworkCall(this, this, JSONObject(), APICode.URL, APICode.GET_MY_PROFILE, isLocation = false, isLoading = true)
    }

    private fun setMemoNotice(noticeTypeID: Int, isChecked: Boolean) {
        val status = if (isChecked) "0" else "1"
        val jsonObject = JSONObject().apply {
            put("notice_type_id", noticeTypeID)
            put("notice_status", status)
        }
        RetrofitClient().newInstance()
            .setNetworkCall(this, this, jsonObject, APICode.URL, APICode.SET_MEMO_NOTICE, isLocation = false, isLoading = false)
    }
    private fun saveSignature(signature: String) {
        val jsonObject = JSONObject().apply {
            put("signature", signature)
        }
        RetrofitClient().newInstance()
            .setNetworkCall(this, this, jsonObject, APICode.URL, APICode.UPLOAD_MY_SIGNATURE, isLocation = false, isLoading = true)
    }

    override fun onSuccessResult(strJson: String, apiCode: String) {
        when (apiCode) {
            APICode.GET_MY_PROFILE -> {
                val model = Gson().fromJson(strJson, CMProfile::class.java)
                when (model.command) {
                    apiCode -> setViewProfile(model)
                    else -> showDialogAlert(false, model.message)
                }
            }
            APICode.SET_MEMO_NOTICE -> {
                val model = Gson().fromJson(strJson, CMModel::class.java)
                when (model.command) {
                    apiCode -> { }
                    else -> showDialogAlertFinish(this, false, model.message)
                }
            }
            APICode.UPLOAD_MY_SIGNATURE -> {
                val model = Gson().fromJson(strJson, CMModel::class.java)
                when (model.command) {
                    apiCode -> {
                        showDialogAlertListener(true, model.message,
                            object : OnDialogDismissListener {
                                override fun onDismiss() {
                                    DialogSignature.dialogSignature!!.dismiss()
                                    returnRefreshActivity()
                                }
                            })
                        setSharePreUser(this, Configs.EmpSignature, model.signature)
                    }
                    else -> showDialogAlert(this, false, model.message)
                }
            }
        }
    }
    override fun onErrorResult(msg: String, apiCode: String) {}

    private fun setViewProfile(model: CMProfile?) {
        val adapter = ProfileAdapter(this, model!!.current_documents)
        adapter.setOnClickListener(this)
        rvList.layoutManager = LinearLayoutManager(this)
        rvList.adapter = adapter

        //set switch notice setting
        for (index in model.notice.indices) {
            val isCheck = model.notice[index].send==1
                when (model.notice[index].id) {
                    Configs.SwitchEmailID -> { takeIf {isCheck}?.apply { switchEmail.isChecked = true }
                        setViewVisible(blockSwitchEmail,true)
                    }
                    Configs.SwitchMemoID -> { takeIf {isCheck}?.apply { switchMemo.isChecked = true }
                        setViewVisible(blockSwitchMemo,true)
                    }
                    Configs.SwitchLineID -> { takeIf {isCheck}?.apply { switchLine.isChecked = true }
                        setViewVisible(blockSwitchLine,true)
                    }
                }
        }
        switchEmail.setOnCheckedChangeListener { _, isChecked ->
            setMemoNotice(Configs.SwitchEmailID, isChecked)
        }
        switchMemo.setOnCheckedChangeListener { _, isChecked ->
            setMemoNotice(Configs.SwitchMemoID, isChecked)
        }
        switchLine.setOnCheckedChangeListener { _, isChecked ->
            setMemoNotice(Configs.SwitchLineID, isChecked)
        }
        setViewVisible(blockContent, true)
    }

    override fun onClickList(model: CurrentDocument) {
        val bundle = Bundle().apply {
            putString("menuName", getString(R.string.action_list))
            putInt("menuAction", Configs.REQUEST_MENU_PROFILE)
            putString("isStatusId", model.status_id.toString())
            putString("isStatusName", model.status_name)
        }
        openActivityWithBundle(MemoStatusActivity::class.java, bundle)
    }

    // Open Dialog Signature onPause App Stop (clear Bundle)
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
    }

    override fun sendPathPicSignature(isSkip: Boolean, input: String?) {
        saveSignature(input!!)
        isLog("pathSignature", input)
    }

}