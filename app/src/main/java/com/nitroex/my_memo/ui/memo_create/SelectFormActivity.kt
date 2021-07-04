package com.nitroex.my_memo.ui.memo_create

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.nitroex.my_memo.BaseActivity
import com.nitroex.memo.my_memo.R
import com.nitroex.my_memo.service.APICode
import com.nitroex.my_memo.service.RetrofitClient
import com.nitroex.my_memo.ui.MainActivity
import com.nitroex.my_memo.ui.memo_create.adapter.CreateMemoAdapter
import com.nitroex.my_memo.ui.memo_create.model.CMGetFormList
import com.nitroex.my_memo.ui.memo_create.model.GetFormList
import com.nitroex.my_memo.utils.Configs
import com.nitroex.my_memo.utils.listener.CommonResponseListener
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_select_form.*
import kotlinx.android.synthetic.main.view_icon_bottom.*
import kotlinx.android.synthetic.main.view_title_head.*
import org.json.JSONObject

class SelectFormActivity : BaseActivity(), CommonResponseListener, CreateMemoAdapter.OnClickListListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_form)

        setClick()
        setView()
    }

    private fun setClick() {
        btnHeadBack.setOnClickListener { onBackPressed() }
        btnFabIcon.setOnClickListener { openActivityFinish(MainActivity::class.java) }
    }

    private fun setView() {
        val bundle = getBundleFromIntent(this)
        if (bundle != null) {
            setTitleAppBar(bundle)
            getFormList()
        }
    }

    private fun getFormList() {
        RetrofitClient().newInstance()
            .setNetworkCall(this, this, JSONObject(), APICode.URL, APICode.GET_FORM_LIST, isLocation = false, isLoading = true)
    }

    override fun onSuccessResult(strJson: String, apiCode: String) {
        val model = Gson().fromJson(strJson, CMGetFormList::class.java)
        when (model.command) {
            apiCode -> setFormList(model.data)
            else -> showDialogAlertFinish(this, false, model.message)
        }
    }
    override fun onErrorResult(msg: String, apiCode: String) {}

    private fun setFormList(model: List<GetFormList>?) {
        val adapter = CreateMemoAdapter(this, model!!)
        adapter.setOnClickListener(this)
        rvList.layoutManager = LinearLayoutManager(this)
        rvList.adapter = adapter

        setHideFabOnScroll(rvList)
    }

    override fun onClickList(model: GetFormList) {
        if (model.mf_id_pk == Configs.INTERNAL_FORM_ID) {
            val bundle = Bundle().apply {
                putString("menuId", model.mf_id_pk)
                putString("isFromType", Configs.NewForm)
            }
            openActivityWithBundle(InternalFormActivity::class.java, bundle)

        }
    }

}