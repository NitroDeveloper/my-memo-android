package com.nitroex.my_memo.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.nitroex.my_memo.BaseActivity
import com.nitroex.memo.my_memo.R
import com.nitroex.my_memo.service.APICode
import com.nitroex.my_memo.service.RetrofitClient
import com.nitroex.my_memo.utils.Configs
import com.nitroex.my_memo.utils.listener.CommonResponseListener
import com.nitroex.my_memo.utils.listener.OnDialogCallbackListener
import com.nitroex.my_memo.utils.model.CMCheckEmail
import com.nitroex.my_memo.utils.model.CMModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.view_forgot_pass_email.*
import kotlinx.android.synthetic.main.view_forgot_pass_new.*
import kotlinx.android.synthetic.main.view_forgot_pass_otp.*
import org.json.JSONObject
import java.util.regex.Pattern

class ForgotPasswordActivity : BaseActivity(), CommonResponseListener {
    private var comId = ""
    private var email = ""
    private var otp = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        setView("email")
        setClick()
    }

    private fun setView(isView: String) {
        setBlockVisibleByType(isView)
    }

    private fun setBlockVisibleByType(isView: String){
        setViewVisible(blockForgotEmail, false)
        setViewVisible(blockForgotOTP, false)
        setViewVisible(blockForgotNewPass, false)

        when (isView) {
            "email" -> { setViewVisible(blockForgotEmail, true) }
            "otp" -> { setViewVisible(blockForgotOTP, true) }
            "pass" -> { setViewVisible(blockForgotNewPass, true) }
        }
    }

    private fun setViewBlockNewPass() {
        val passNew = getTextToTrim(edtNewPass)
        val passCf = getTextToTrim(edtNewPassCf)

        if (isValidPassword(passNew, passCf)) {
            changePasswordApi(passNew)
        }
    }

    private fun isValidPassword(passNew: String, passCf: String): Boolean {
        var isValid = false
        val upperCasePatten = Pattern.compile("[A-Z ]")
        val lowerCasePatten = Pattern.compile("[a-z ]")
        val digitCasePatten = Pattern.compile("[0-9 ]")

        if (passNew.length in 8..16) {
            if (upperCasePatten.matcher(passNew).find()) {
                if (lowerCasePatten.matcher(passNew).find()) {
                    if (digitCasePatten.matcher(passNew).find()) {
                        if (passNew==passCf){
                            isValid = true
                        }else{
                            showDialogAlert(false, getString(R.string.tv_error_confirm))
                        }
                    } else {
                        showDialogAlert(false, getString(R.string.tv_error_digitcharacter))
                    }
                } else {
                    showDialogAlert(false, getString(R.string.tv_error_lowercasecharacter))
                }
            } else {
                showDialogAlert(false, getString(R.string.tv_error_uppercasecharacter ))
            }
        } else {
            showDialogAlert(false, getString(R.string.tv_error_character ))
        }
        return isValid
    }

    private fun setClick() {
        btnSubmitEmail.setOnClickListener {
            if (checkTextIsNotEmptyAlert(getTextToTrim(edtEmail), getString(R.string.please_fill_email), false)) {
                email = getTextToTrim(edtEmail)
                checkEmailApi()
            }
        }
        btnSubmitOtp.setOnClickListener {
            if (checkTextIsNotEmptyAlert(getTextToTrim(edtOtp), getString(R.string.enter_otp), false)) {
                otp = getTextToTrim(edtOtp)
                checkOtpApi()
            }
        }
        btnSubmitNewPass.setOnClickListener {
            setViewBlockNewPass()
        }
    }

    private fun checkEmailApi() {
        val jsonObject = JSONObject().apply {
            put("email", email)
        }
        RetrofitClient().newInstance()
            .setNetworkCall(this, this, jsonObject, APICode.URL, APICode.CHECK_EMAIL, isLocation = false, isLoading = true)
    }

    private fun forgotPasswordApi() {
        val jsonObject = JSONObject().apply {
            put("company_id", comId)
            put("email", email)
        }
        RetrofitClient().newInstance()
            .setNetworkCall(this, this, jsonObject, APICode.URL, APICode.FORGOT_PASSWORD, isLocation = false, isLoading = true)
    }

    private fun checkOtpApi() {
        val jsonObject = JSONObject().apply {
            put("company_id", comId)
            put("otp_code", otp)
            put("otp_email", email)
        }
        RetrofitClient().newInstance()
            .setNetworkCall(this, this, jsonObject, APICode.URL, APICode.CHECK_OTP_FORGOT_PASSWORD, isLocation = false, isLoading = true)
    }

    private fun changePasswordApi(passNew: String) {
        val jsonObject = JSONObject().apply {
            put("email", email)
            put("password", passNew)
            put("company_id", comId)
        }
        RetrofitClient().newInstance()
            .setNetworkCall(this, this, jsonObject, APICode.URL, APICode.CHANGE_PASSWORD_BY_FORGOT_PASSWORD, isLocation = false, isLoading = true)
    }

    override fun onSuccessResult(strJson: String, apiCode: String) {
        when (apiCode) {
            APICode.CHECK_EMAIL -> {
                val model = Gson().fromJson(strJson, CMCheckEmail::class.java)
                when (model.command) {
                    apiCode -> {
                        if (model.data.size==1) {
                            comId = model.data[0].company_id
                            forgotPasswordApi()
                        }else{
                            val bundle = Bundle().apply {
                                putString("isFrom", "ForgotPass")
                                putSerializable("model", model.data)
                            }
                            openActivityWithBundleForResult(CompanyActivity::class.java, bundle, Configs.REQUEST_SELECT_COMPANY)
                        }
                    }
                    else -> showDialogAlert(false, model.message)
                }
            }
            APICode.FORGOT_PASSWORD -> {
                val model = Gson().fromJson(strJson, CMModel::class.java)
                when (model.command) {
                    apiCode -> {
                        showDialogAlert(true, model.message)
                        setView("otp")
                    }
                    else -> showDialogAlert(false, model.message)
                }
            }
            APICode.CHECK_OTP_FORGOT_PASSWORD -> {
                val model = Gson().fromJson(strJson, CMModel::class.java)
                when (model.command) {
                    apiCode -> { setView("pass") }
                    else -> showDialogAlert(false, model.message)
                }
            }
            APICode.CHANGE_PASSWORD_BY_FORGOT_PASSWORD -> {
                val model = Gson().fromJson(strJson, CMModel::class.java)
                when (model.command) {
                    apiCode -> showDialogAlertFinish(this, true, model.message)
                    else -> showDialogAlert(false, model.message)
                }
            }
        }
    }
    override fun onErrorResult(msg: String, apiCode: String) {}

    override fun onBackPressed() {
        showDialogConfirm(object : OnDialogCallbackListener {
            override fun onSubmit() { finish() }
            override fun onCancel() {}
        }, getString(R.string.want_to_exit_forgot_pass))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        when (requestCode) {
            Configs.REQUEST_SELECT_COMPANY -> {
                comId = getStringFromBundle("company_id", data!!)
                forgotPasswordApi()
            }
        }
    }
}