package com.nitroex.my_memo.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.nitroex.my_memo.BaseActivity
import com.nitroex.memo.my_memo.R
import com.nitroex.my_memo.service.APICode
import com.nitroex.my_memo.service.RetrofitClient
import com.nitroex.my_memo.ui.memo_create.SelectFormActivity
import com.nitroex.my_memo.ui.memo_draft.DraftMemoActivity
import com.nitroex.my_memo.ui.memo_favorite.FavoriteMemoActivity
import com.nitroex.my_memo.ui.memo_status.MemoStatusActivity
import com.nitroex.my_memo.utils.adapter.CompanyAdapter
import com.nitroex.my_memo.utils.adapter.MainMenuAdapter
import com.nitroex.my_memo.utils.model.CMLogin
import com.nitroex.my_memo.utils.model.CMModel
import com.nitroex.my_memo.utils.CameraUtils
import com.nitroex.my_memo.utils.Configs
import com.nitroex.my_memo.utils.listener.CommonResponseListener
import com.nitroex.my_memo.utils.listener.OnCameraCallbackListener
import com.nitroex.my_memo.utils.listener.OnDialogCallbackListener
import com.nitroex.my_memo.utils.model.CMProfile
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_main_menu.view.*
import org.json.JSONObject

class MainActivity : BaseActivity(), CommonResponseListener, MainMenuAdapter.OnMenuClickListener {
    private lateinit var cameraUtils: CameraUtils
    private var isManyCompany = false
    private var isShowLang = ""
    private var isProfilePath = ""
    private lateinit var adtMenu: MainMenuAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setView()
        setClick()
        setAdapterMenu()
        setAdapterCompany()
        getSignatureApi()
    }

    @SuppressLint("SetTextI18n")
    private fun setView() {
        val user = getDataLogin().data[0]
        if (getDataCompany().size>1) isManyCompany=true

        setPicGlideProfile(this, user.emp_profile_image, ivProfile)
        setBlockSignature(getSignatureSP(), blockSignature, ivSignature)

        tvVersion.text = "V "+ getVersionApp()
        tvFullName.text = user.emp_name+ " (" + user.emp_com_id + ")"
        tvCompany.text = getCompanyName()

        cameraUtils = CameraUtils().newInstance()
        cameraUtils.setEasyImage(this)
    }

    private fun setClick() {
        btnLogout.setOnClickListener {
            showDialogConfirm(object : OnDialogCallbackListener {
                override fun onSubmit() { setLogoutApi() }
                override fun onCancel() {}
            }, getString(R.string.txt_alert_sign_out_main))
        }
        btnLangTH.setOnClickListener {
            if (getCurrentLanguage().language != Configs.Thai && isShowLang.isEmpty()) {
                isShowLang = Configs.Thai
                loginApi()
            }
        }
        btnLangEN.setOnClickListener {
            if (getCurrentLanguage().language != Configs.English && isShowLang.isEmpty()) {
                isShowLang = Configs.English
                loginApi()
            }
        }
        ivProfile.setOnClickListener {
            cameraUtils.showDialogSelectPic(object : OnCameraCallbackListener {
                override fun onCameraSuccess(isPath: String) {
                    isProfilePath = isPath
                    uploadProfileImage(isPath)
                }
                override fun onCameraError(msg: String) {
                    showDialogAlert(false, msg)
                }
            })
        }
        if (isManyCompany) {
            tvCompany.setOnClickListener { autoCompany.showDropDown() }
            ivSelect.setOnClickListener { autoCompany.showDropDown() }
        }
    }

    private fun setAdapterMenu() {
        adtMenu = MainMenuAdapter(this, getDataMenu())
        adtMenu.setOnClickListener(this)
        rvMenu.layoutManager = LinearLayoutManager(this)
        rvMenu.adapter = adtMenu
    }
    override fun onClickMenu(position: Int, menuId: Int, menuName: String) {
        val bundle = Bundle()
        bundle.putString("menuName", menuName)
        when (menuId) {
            Configs.MenuProfile -> {// menu my_profile
                openActivityWithBundleForResult(ProfileActivity::class.java, bundle, Configs.REQUEST_MENU_PROFILE)
            }
            Configs.MenuCreateMemo -> { // menu create_memo
                openActivityWithBundleForResult(SelectFormActivity::class.java, bundle, Configs.REQUEST_MENU_CREATE_MEMO)
            }
            Configs.MenuDraftMemo -> {// menu draft_memo
                openActivityWithBundleForResult(DraftMemoActivity::class.java, bundle, Configs.REQUEST_MENU_DRAFT_MEMO)
            }
            Configs.MenuStatusMemo -> {// menu memo_status
                bundle.putInt("menuAction", Configs.REQUEST_MENU_STATUS_MEMO)
                openActivityWithBundleForResult(MemoStatusActivity::class.java, bundle, Configs.REQUEST_MENU_STATUS_MEMO)
            }
            Configs.MenuFavoriteMemo -> {// menu favorites_form
                openActivityWithBundleForResult(FavoriteMemoActivity::class.java, bundle, Configs.REQUEST_MENU_FAVORITE_MEMO)
            }
            Configs.MenuToDoList -> {// menu todo_list
                bundle.putInt("menuAction", Configs.REQUEST_MENU_TODO_LIST_MEMO)
                openActivityWithBundleForResult(MemoStatusActivity::class.java, bundle, Configs.REQUEST_MENU_TODO_LIST_MEMO)
            }
        }
    }

    private fun setAdapterCompany() {
        if (isManyCompany) {
            setViewVisible(ivSelect, true)
            val adapter = CompanyAdapter(this, R.layout.list_view_row, getDataCompany())
            autoCompany.setAdapter(adapter)
            autoCompany.setOnItemClickListener { _, _, position, _ ->
                autoCompany.setText("")
                val model = getDataCompany()[position]
                tvCompany.text = model.company_name

                setSharePreUserByCompany(model.emp_id_pk, model.emp_com_id, model.com_id_pk, model.company_name)

                setAdapterMenu()
            }
        }else {
            setViewVisible(ivSelect, false)
        }
    }

    private fun setLogoutApi() {
        RetrofitClient().newInstance()
            .setNetworkCall(this, this, JSONObject(), APICode.URL, APICode.LOGOUT, isLocation = false, isLoading = true)
    }

    private fun uploadProfileImage(isPath: String) {
        val jsonObject = JSONObject().apply {
            put("profile_image_path", isPath)
        }
        RetrofitClient().newInstance()
            .setNetworkCall(this, this, jsonObject, APICode.URL, APICode.UPDATE_PROFILE_IMAGE, isLocation = false, isLoading = true)
    }

    private fun loginApi() {
        setViewVisible(ivLoading,true)
        val username = getSharePreUser(this, Configs.UserName, "")
        val password = getSharePreUser(this, Configs.Password, "")
        val jsonObject = JSONObject().apply {
            put("username", username)
            put("password", password)
            put("language", isShowLang)
        }
        RetrofitClient().newInstance()
            .setNetworkCall(this, this, jsonObject, APICode.URL, APICode.LOGIN, isLocation = false, isLoading = false)
    }

    private fun getUserProfile() {
        RetrofitClient().newInstance()
            .setNetworkCall(this, this, JSONObject(), APICode.URL, APICode.GET_MY_PROFILE, isLocation = false, isLoading = false)
    }

    private fun getSignatureApi() {
        RetrofitClient().newInstance()
            .setNetworkCall(this, this, JSONObject(), APICode.URL, APICode.GET_MY_SIGNATURE, isLocation = false, isLoading = false)
    }

    override fun onSuccessResult(strJson: String, apiCode: String) {
        when (apiCode) {
            APICode.LOGOUT -> { clearSharePreUser(); openActivityFinish(SplashScreenActivity::class.java) }
            APICode.UPDATE_PROFILE_IMAGE -> {
                val model = Gson().fromJson(strJson, CMModel::class.java)
                when (model.command) {
                    apiCode -> {
                        setPicGlideProfile(this, isProfilePath, ivProfile)
                        setSharePreUser(this, Configs.ProfilePic, model.emp_profile_image)
                    }
                    else -> showDialogAlert(false, model.message)
                }
            }
            APICode.LOGIN -> {
                setViewVisible(ivLoading,false)
                val model = Gson().fromJson(strJson, CMLogin::class.java)
                when (model.command) {
                    apiCode -> {
                        setSharePreUser(this, Configs.JsonLogin, strJson)
                        setLanguage(isShowLang)
                    }
                    else -> {
                        openActivityFinish(LoginActivity::class.java)
                        clearSharePreUser()
                    }
                }
            }
            APICode.GET_MY_PROFILE -> {
                val model = Gson().fromJson(strJson, CMProfile::class.java)
                when (model.command) {
                    apiCode -> adtMenu.setItem(model.wait_for_agree_total + model.wait_for_approve_total)
                    else -> showDialogAlert(false, model.message)
                }
            }
            APICode.GET_MY_SIGNATURE -> {
                val model = Gson().fromJson(strJson, CMModel::class.java)
                when (model.command) {
                    apiCode -> {
                        if (checkTextIsNotEmpty(model.signature)) {
                            if (getSignatureSP() != model.signature) {
                                setSharePreUser(this, Configs.EmpSignature, model.signature)
                                setBlockSignature(model.signature, blockSignature, ivSignature)
                            }
                        }else{
                            setSharePreUser(this, Configs.EmpSignature, "")
                            setViewVisible(blockSignature, false)
                        }
                    }
                }
            }
        }
    }

    override fun onErrorResult(msg: String, apiCode: String) { setViewVisible(ivLoading,false) }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) return
        when (requestCode) {
            Configs.REQUEST_MENU_PROFILE -> {
                val signature = getSharePreUser(this, Configs.EmpSignature, "")
                if (signature.isNotEmpty()) { setPicGlide(this, convertSignatureStringToByte(signature), ivSignature) }
            }
            else -> { // setActivity CameraConfig
                cameraUtils.setActivityResult(requestCode, resultCode, data)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getUserProfile()
    }

    override fun onBackPressed() {
        showDialogConfirm(object : OnDialogCallbackListener {
            override fun onSubmit() { exitApp(this@MainActivity) }
            override fun onCancel() {}
        },getString(R.string.want_to_exit))
    }


}