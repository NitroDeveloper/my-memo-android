package com.nitroex.my_memo.ui

import android.os.Bundle
import com.nitroex.my_memo.BaseActivity
import com.nitroex.memo.my_memo.R
import com.nitroex.my_memo.service.APICode
import com.nitroex.my_memo.service.RetrofitClient
import com.nitroex.my_memo.utils.model.CMLogin
import com.nitroex.my_memo.utils.model.CMModel
import com.nitroex.my_memo.utils.Configs
import com.nitroex.my_memo.utils.listener.CommonResponseListener
import com.nitroex.my_memo.utils.listener.OnDialogCallbackListener
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

class LoginActivity : BaseActivity(), CommonResponseListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setView()
    }

    private fun setView() {
        if (isLogin()) {
            setContentView(R.layout.activity_splash_screen)
            loginApi()
        }else{
            setContentView(R.layout.activity_login)
            setClick()
            clearSharePreUser()
        }
    }

    private fun setClick() {
        btnLogin.setOnClickListener { checkInputForm() }
        btnForgotPass.setOnClickListener { openActivity(ForgotPasswordActivity::class.java)}
    }

    private fun checkInputForm() {
        if (getTextToTrim(edtUsername).isNotEmpty() && getTextToTrim(edtPassword).isNotEmpty()) {
            loginApi()
        }else{
            showDialogAlert(this, false, getString(R.string.please_input_fill))
        }
    }

    private fun loginApi() {
        val username: String
        val password: String
        val isLoading: Boolean

        if (isLogin()) {
            username = getSharePreUser(this, Configs.UserName,"")
            password = getSharePreUser(this, Configs.Password,"")
            isLoading = false
        }else{
            username = getTextToTrim(edtUsername)
            password = getTextToTrim(edtPassword)
            isLoading = true
        }

        val jsonObject = JSONObject().apply {
            put("username", username)
            put("password", password)
        }
        RetrofitClient().newInstance()
            .setNetworkCall(this, this, jsonObject, APICode.URL, APICode.LOGIN, isLocation = false, isLoading = isLoading)
    }

    private fun setLogoutApi(userName: String, comId: String) {
        val jsonObject = JSONObject().apply {
            put("username", userName)
            put("company_id", comId)
        }
        RetrofitClient().newInstance()
            .setNetworkCall(this, this, jsonObject, APICode.URL, APICode.SET_LOGOUT, isLocation = false, isLoading = true)
    }

    override fun onSuccessResult(strJson: String, apiCode: String) {
        when (apiCode) {
            APICode.LOGIN -> {
                val model = Gson().fromJson(strJson, CMLogin::class.java)

                if (isLogin()) {
                    when (model.command) {
                        apiCode -> setDataUser(model, strJson)
                        else -> {
                            openActivityFinish(LoginActivity::class.java)
                            clearSharePreUser()
                        }
                    }
                }else{
                    when (model.command) {
                        apiCode -> setDataUser(model, strJson)
                        APICode.IS_LOGIN -> showDialogForceLogout(getTextToTrim(edtUsername), model.company_id)
                        else -> showDialogAlert(false, model.message)
                    }
                }

            }
            APICode.SET_LOGOUT -> {
                val model = Gson().fromJson(strJson, CMModel::class.java)
                when (model.command) {
                    apiCode -> loginApi()
                    else -> showDialogAlert(false, model.message)
                }
            }
        }
    }
    override fun onErrorResult(msg: String, apiCode: String) {}

    private fun setDataUser(cmLogin: CMLogin, strJson: String) {
        setSharePreUser(this, Configs.JsonLogin, strJson)
        setSharePreUser(this, Configs.ProfilePic, cmLogin.data[0].emp_profile_image)

        if (isLogin()){ // Auto Login
            if (getSharePreUser(this, Configs.IsSelectCompany, false))  // CheckIsSelectCompany
                openActivityFinish(MainActivity::class.java)
            else checkCompany(cmLogin)

        }else{ // First Login
            setSharePreUser(this, Configs.IsLogin, true)
            setSharePreUser(this, Configs.UserName, getTextToTrim(edtUsername))
            setSharePreUser(this, Configs.Password, getTextToTrim(edtPassword))
            checkCompany(cmLogin)
        }
    }

    private fun checkCompany(cmLogin: CMLogin) {
        if(cmLogin.company.isNotEmpty() && cmLogin.company.size > 1){
            openActivityFinish(this, CompanyActivity::class.java)
        } else {
            val model = cmLogin.company[0]
            setSharePreUserByCompany(model.emp_id_pk, model.emp_com_id, model.com_id_pk, model.company_name)
            setSharePreUser(this, Configs.IsSelectCompany, true)
            openActivityFinish(MainActivity::class.java)
        }
    }

    private fun showDialogForceLogout(userName: String, comId: String) {
        showDialogConfirm(object : OnDialogCallbackListener {
            override fun onSubmit() { setLogoutApi(userName, comId) }
            override fun onCancel() {}
        }, getString(R.string.is_user_login))
    }
}