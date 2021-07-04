package com.nitroex.my_memo.service

import android.app.Dialog
import android.content.Context
import com.nitroex.my_memo.BaseActivity
import com.nitroex.memo.my_memo.R
import com.nitroex.my_memo.utils.Configs
import com.nitroex.my_memo.utils.listener.CommonResponseListener
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.*
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.jetbrains.anko.toast
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.util.concurrent.TimeUnit

class RetrofitClient: BaseActivity() {
    private var instance: RetrofitClient? = null
    private lateinit var isListener : CommonResponseListener
    private lateinit var isData: JSONObject
    private lateinit var context: Context
    private lateinit var isBaseUrl: String
    private lateinit var isApiCode: String

    private var isLoading: Boolean = false
    private lateinit var loading: Dialog
    val apiSentMemo = listOf(
        APICode.INSERT_MEMO_NEW,
        APICode.REVISE_MEMO,
        APICode.INSERT_MEMO_FROM_DRAFT,
        APICode.SAVE_DRAFT_MEMO
    )

    fun newInstance(): RetrofitClient {
        if (instance == null) instance = RetrofitClient()
        return instance as RetrofitClient
    }

    fun setNetworkCall(
        context: Context,
        listener: CommonResponseListener,
        data: JSONObject,
        baseUrl: String,
        apiCode: String,
        isLocation: Boolean,
        isLoading: Boolean
    ) {
        this.context = context; this.isListener = listener; this.isData = data; this.isBaseUrl = baseUrl; this.isApiCode = apiCode; this.isLoading = isLoading

        checkInternetConnection()
    }

    private fun checkInternetConnection() {
        if (isInternetAvailable(context)) {
            if (isLoading) {
                loading = loadingDialog(context, true)
                try { loading.show() } catch (e: Exception) { }
            }
            commonNetworkCall()
        }else{
            context.toast(context.getString(R.string.please_check_internet))
            try { isListener.onErrorResult("", "") } catch (e: Exception) { }
        }
    }

    private fun commonNetworkCall() {
        val gson = GsonBuilder().setLenient().create()
        val okHttpClient = OkHttpClient.Builder()
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)

        // show logging connect https debug only
        if (isBuildDebug()) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            okHttpClient.addInterceptor(logging)
        } // show logging connect https debug only

        okHttpClient.apply {
            connectTimeout(60, TimeUnit.SECONDS)
            readTimeout(60, TimeUnit.SECONDS)
            retryOnConnectionFailure(true)
        }
        // default value data
        val isDeviceToken = getSharePreSystem(context, Configs.DeviceToken, "")
        val language = if (getStringJsonObj(isData, "language").isNotEmpty())
            getStringJsonObj(isData, "language")
        else (context as BaseActivity).getCurrentLanguage().language
        isLogDebug("$isApiCode: language", language)

        builder.addFormDataPart("command", isApiCode)
        builder.addFormDataPart("language", language)
        builder.addFormDataPart("device_token", isDeviceToken)
        builder.addFormDataPart("device_type", Configs.DEVICE_TYPE)
        builder.addFormDataPart("username", getSharePreUser(context, Configs.UserName, ""))
        builder.addFormDataPart("employee_code", getSharePreUser(context, Configs.EmpComID, ""))
        builder.addFormDataPart("employee_id", getSharePreUser(context, Configs.EmpComID, ""))
        builder.addFormDataPart("company_id", getSharePreUser(context, Configs.CompanyID, ""))
        builder.addFormDataPart("memo_form_id", getStringJsonObj(isData, "memo_form_id"))

        if (isApiCode == APICode.LOGIN) {
            FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    isLog(
                        "FcmToken",
                        task.exception.toString()
                    ); context.toast("GetInstanceId FcmToken Failed !!")
                    return@OnCompleteListener
                }
                val token = task.result?.token.toString()
                builder.addFormDataPart("push_token", token)

                connectRetrofit(gson, okHttpClient, builder)
            })
        }else{
            connectRetrofit(gson, okHttpClient, builder)
        }
    }

    private fun connectRetrofit(
        gson: Gson,
        okHttpClient: OkHttpClient.Builder,
        builder: MultipartBody.Builder
    ) {
        val retrofit = Retrofit.Builder()
            .baseUrl(isBaseUrl)
            .client(okHttpClient.build())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        val retrofitApi = retrofit.create(RetrofitApi::class.java)

        when (isApiCode) {
            APICode.LOGIN -> {
                builder.addFormDataPart("username", getStringJsonObj(isData, "username"))
                builder.addFormDataPart("password", getStringJsonObj(isData, "password"))
                requestRetrofit(retrofitApi.getResponseBody(builder.build()), builder.toString())
            }
            APICode.SET_LOGOUT -> {
                builder.addFormDataPart("username", getStringJsonObj(isData, "username"))
                builder.addFormDataPart("company_id", getStringJsonObj(isData, "company_id"))
                requestRetrofit(retrofitApi.getResponseBody(builder.build()), builder.toString())
            }
            APICode.LOGOUT -> {
                requestRetrofit(retrofitApi.getResponseBody(builder.build()), builder.toString())
            }
            APICode.CHECK_EMAIL -> {
                builder.addFormDataPart("email", getStringJsonObj(isData, "email"))
                requestRetrofit(retrofitApi.getResponseBody(builder.build()), builder.toString())
            }
            APICode.FORGOT_PASSWORD -> {
                builder.addFormDataPart("company_id", getStringJsonObj(isData, "company_id"))
                builder.addFormDataPart("email", getStringJsonObj(isData, "email"))
                requestRetrofit(retrofitApi.getResponseBody(builder.build()), builder.toString())
            }
            APICode.CHECK_OTP_FORGOT_PASSWORD -> {
                builder.addFormDataPart("company_id", getStringJsonObj(isData, "company_id"))
                builder.addFormDataPart("otp_code", getStringJsonObj(isData, "otp_code"))
                builder.addFormDataPart("otp_email", getStringJsonObj(isData, "otp_email"))
                requestRetrofit(retrofitApi.getResponseBody(builder.build()), builder.toString())
            }
            APICode.CHANGE_PASSWORD_BY_FORGOT_PASSWORD -> {
                builder.addFormDataPart("email", getStringJsonObj(isData, "email"))
                builder.addFormDataPart("password", getStringJsonObj(isData, "password"))
                builder.addFormDataPart("company_id", getStringJsonObj(isData, "company_id"))
                requestRetrofit(retrofitApi.getResponseBody(builder.build()), builder.toString())
            }
            APICode.UPDATE_PROFILE_IMAGE -> {
                val file = File(getStringJsonObj(isData, "profile_image_path"))
                builder.addFormDataPart(
                    "profile_image_path", file.name, RequestBody.create(
                        MediaType.parse(
                            "multipart/form-data"
                        ), file
                    )
                )
                requestRetrofit(retrofitApi.getResponseBody(builder.build()), builder.toString())
            }
            APICode.GET_MY_PROFILE -> {
                requestRetrofit(retrofitApi.getResponseBody(builder.build()), builder.toString())
            }
            APICode.SET_MEMO_NOTICE -> {
                builder.addFormDataPart("notice_type_id", getStringJsonObj(isData, "notice_type_id"))
                builder.addFormDataPart("notice_status", getStringJsonObj(isData, "notice_status"))
                requestRetrofit(retrofitApi.getResponseBody(builder.build()), builder.toString())
            }
            APICode.GET_MY_SIGNATURE -> {
                requestRetrofit(retrofitApi.getResponseBody(builder.build()), builder.toString())
            }
            APICode.UPLOAD_MY_SIGNATURE -> {
                if (getStringJsonObj(isData, "signature").isNotEmpty()) {
                    val file = File(getStringJsonObj(isData, "signature"))
                    builder.addFormDataPart(
                        "signature", file.name, RequestBody.create(
                            MediaType.parse(
                                "multipart/form-data"
                            ), file
                        )
                    )
                }
                requestRetrofit(retrofitApi.getResponseBody(builder.build()), builder.toString())
            }
            APICode.GET_FORM_LIST -> {
                requestRetrofit(retrofitApi.getResponseBody(builder.build()), builder.toString())
            }
            APICode.GET_MEMO_NO_LIST -> {
                builder.addFormDataPart("memo_format_lang", getStringJsonObj(isData, "memo_format_lang"))
                requestRetrofit(retrofitApi.getResponseBody(builder.build()), builder.toString())
            }
            APICode.GET_APPROVE_LIST -> {
                requestRetrofit(retrofitApi.getResponseBody(builder.build()), builder.toString())
            }
            APICode.GET_FAVORITE_LIST -> {
                builder.addFormDataPart("form_id", "0")
                requestRetrofit(retrofitApi.getResponseBody(builder.build()), builder.toString())
            }
            APICode.GET_DRAFT_LIST -> {
                requestRetrofit(retrofitApi.getResponseBody(builder.build()), builder.toString())
            }
            APICode.DELETE_DRAFT_MEMO -> {
                builder.addFormDataPart("memo_id", getStringJsonObj(isData, "memo_id"))
                requestRetrofit(retrofitApi.getResponseBody(builder.build()), builder.toString())
            }
            APICode.FAVORITE_MEMO -> {
                builder.addFormDataPart("memo_id", getStringJsonObj(isData, "memo_id"))
                requestRetrofit(retrofitApi.getResponseBody(builder.build()), builder.toString())
            }
            APICode.GET_MEMO_STATUS_LIST -> {
                requestRetrofit(retrofitApi.getResponseBody(builder.build()), builder.toString())
            }
            APICode.GET_MEMO_DETAIL_NEW -> {
                builder.addFormDataPart("memo_id", getStringJsonObj(isData, "memo_id"))
                requestRetrofit(retrofitApi.getResponseBody(builder.build()), builder.toString())
            }
            APICode.CANCEL_MEMO -> {
                builder.addFormDataPart("memo_id", getStringJsonObj(isData, "memo_id"))
                builder.addFormDataPart("memo_comment", getStringJsonObj(isData, "memo_comment"))
                requestRetrofit(retrofitApi.getResponseBody(builder.build()), builder.toString())
            }
            APICode.APPROVE_MEMO -> {
                builder.addFormDataPart("memo_id", getStringJsonObj(isData, "memo_id"))
                builder.addFormDataPart("memo_comment", getStringJsonObj(isData, "memo_comment"))

                // attach_file_signature
                val signature = getStringJsonObj(isData, "attach_file_signature")
                if (signature.isNotEmpty()) {
                    val file = File(signature)
                    builder.addFormDataPart(
                        "attach_file_signature", file.name, RequestBody.create(
                            MediaType.parse(
                                "multipart/form-data"
                            ), file
                        )
                    )
                }
                requestRetrofit(retrofitApi.getResponseBody(builder.build()), builder.toString())
            }
            APICode.DISAPPROVE_MEMO -> {
                builder.addFormDataPart("memo_id", getStringJsonObj(isData, "memo_id"))
                builder.addFormDataPart("memo_comment", getStringJsonObj(isData, "memo_comment"))

                // attach_file_signature
                val signature = getStringJsonObj(isData, "attach_file_signature")
                if (signature.isNotEmpty()) {
                    val file = File(signature)
                    builder.addFormDataPart(
                        "attach_file_signature", file.name, RequestBody.create(
                            MediaType.parse(
                                "multipart/form-data"
                            ), file
                        )
                    )
                }
                requestRetrofit(retrofitApi.getResponseBody(builder.build()), builder.toString())
            }
            APICode.GET_EXPORT_URL -> {
                builder.addFormDataPart("memo_id", getStringJsonObj(isData, "memo_id"))
                requestRetrofit(retrofitApi.getResponseBody(builder.build()), builder.toString())
            }
            APICode.GET_MEMO_HISTORY -> {
                builder.addFormDataPart("memo_id", getStringJsonObj(isData, "memo_id"))
                requestRetrofit(retrofitApi.getResponseBody(builder.build()), builder.toString())
            }
            APICode.RESENT_MEMO -> {
                builder.addFormDataPart("memo_id", getStringJsonObj(isData, "memo_id"))
                requestRetrofit(retrofitApi.getResponseBody(builder.build()), builder.toString())
            }
            APICode.DELETE_ATTACH_FILE -> {
                builder.addFormDataPart("memo_id", getStringJsonObj(isData, "memo_id"))
                builder.addFormDataPart("memo_attach_file_id", getStringJsonObj(isData, "memo_attach_file_id"))
                builder.addFormDataPart("memo_attach_file_name", getStringJsonObj(isData, "memo_attach_file_name"))
                requestRetrofit(retrofitApi.getResponseBody(builder.build()), builder.toString())
            }
            APICode.GET_TITLE_FORM -> {
                builder.addFormDataPart("memo_form_id", getStringJsonObj(isData, "memo_form_id"))
                requestRetrofit(retrofitApi.getResponseBody(builder.build()), builder.toString())
            }
            APICode.GET_MEMO_LIST -> {
                builder.addFormDataPart("division_id", getStringJsonObj(isData, "division_id"))
                builder.addFormDataPart("department_id", getStringJsonObj(isData, "department_id"))
                builder.addFormDataPart("section_id", getStringJsonObj(isData, "section_id"))
                builder.addFormDataPart("memo_key", getStringJsonObj(isData, "memo_key"))
                builder.addFormDataPart("memo_type_id", getStringJsonObj(isData, "memo_type_id"))
                builder.addFormDataPart("start_date", getStringJsonObj(isData, "start_date"))
                builder.addFormDataPart("end_date", getStringJsonObj(isData, "end_date"))
                builder.addFormDataPart("memo_status_id", getStringJsonObj(isData, "memo_status_id"))
                builder.addFormDataPart("action_emp_com_id", getStringJsonObj(isData, "action_emp_com_id"))
                builder.addFormDataPart("my_action", getStringJsonObj(isData, "my_action"))
                builder.addFormDataPart("offset", getStringJsonObj(isData, "offset"))
                builder.addFormDataPart("limit", getStringJsonObj(isData, "limit"))
                requestRetrofit(retrofitApi.getResponseBody(builder.build()), builder.toString())
            }
            APICode.UPDATE_ATTACH_FILES -> {
                builder.addFormDataPart("memo_id", getStringJsonObj(isData, "memo_id"))
                // delete_file
                if (getStringJsonObj(isData, "attach_file_dels").isNotEmpty()) {
                    builder.addFormDataPart(
                        "attach_file_dels", getStringJsonObj(isData, "attach_file_dels")
                    )
                }
                // attach_file
                val fileSize = getStringJsonObj(isData, "attach_file_size").toInt()
                if (fileSize > 0) {
                    for (i in 0 until fileSize) {
                        val file = File(getStringJsonObj(isData, "attach_file_$i"))
                        builder.addFormDataPart(
                            "attach_file_$i", file.name, RequestBody.create(
                                MediaType.parse(
                                    "multipart/form-data"
                                ), file
                            )
                        )
                    }
                }
                //edit_attachment
                if (getBooleanJsonObj(isData, "isEditAttachment")) {
                    builder.addFormDataPart("memo_attachment", getStringJsonObj(isData, "memo_attachment"))
                }

                requestRetrofit(retrofitApi.getResponseBody(builder.build()), builder.toString())
            }
            else -> {

                if (isApiCode in apiSentMemo != getBooleanJsonObj(isData,"memo_form_id")){

                    builder.addFormDataPart("memo_format_lang", getStringJsonObj(isData, "memo_format_lang"))
                    builder.addFormDataPart("memo_id", getStringJsonObj(isData, "memo_id"))
                    builder.addFormDataPart("secret_level", getStringJsonObj(isData, "secret_level"))
                    builder.addFormDataPart("urgent_level", getStringJsonObj(isData, "urgent_level"))
                    builder.addFormDataPart("memo_government", getStringJsonObj(isData, "memo_government"))
                    builder.addFormDataPart("memo_format_lang", getStringJsonObj(isData, "memo_format_lang"))
                    builder.addFormDataPart("memo_no_id", getStringJsonObj(isData, "memo_no_id"))
                    builder.addFormDataPart("memo_date", getStringJsonObj(isData, "memo_date"))
                    builder.addFormDataPart("memo_subject", getStringJsonObj(isData, "memo_subject"))
                    builder.addFormDataPart("to_employee", getStringJsonObj(isData, "to_employee"))
                    builder.addFormDataPart("is_show_to", getStringJsonObj(isData, "is_show_to"))
                    builder.addFormDataPart("memo_show_to", getStringJsonObj(isData, "memo_show_to"))
                    builder.addFormDataPart("memo_attachment", getStringJsonObj(isData, "memo_attachment"))
                    builder.addFormDataPart("memo_format_lang", getStringJsonObj(isData, "memo_format_lang"))
                    builder.addFormDataPart("from_name", getStringJsonObj(isData, "from_name"))
                    builder.addFormDataPart("from_type", getStringJsonObj(isData, "from_type"))
                    builder.addFormDataPart("from_position", getStringJsonObj(isData, "from_position"))
                    builder.addFormDataPart("memo_detail", getStringJsonObj(isData, "memo_detail"))
                    builder.addFormDataPart("mm_create_channel", getStringJsonObj(isData, "mm_create_channel"))

                    if (isApiCode == APICode.REVISE_MEMO)
                        builder.addFormDataPart("is_revise", "1")

                    // attach_file_signature
                    val signature = getStringJsonObj(isData, "attach_file_signature")
                    if (signature.isNotEmpty()) {
                        val file = File(signature)
                        builder.addFormDataPart(
                            "attach_file_signature", file.name, RequestBody.create(
                                MediaType.parse(
                                    "multipart/form-data"
                                ), file
                            )
                        )
                    }
                    // attach_file_picture
                    val fileSize = getStringJsonObj(isData, "attach_file_size").toInt()
                    if (fileSize > 0) {
                        for (i in 0 until fileSize) {
                            val file = File(getStringJsonObj(isData, "attach_file_$i"))
                            builder.addFormDataPart(
                                "attach_file_$i", file.name, RequestBody.create(
                                    MediaType.parse(
                                        "multipart/form-data"
                                    ), file
                                )
                            )
                        }
                    }
                    requestRetrofit(
                        retrofitApi.getResponseBody(builder.build()),
                        builder.toString()
                    )
                }
            }
        }
    }

    private fun requestRetrofit(call: Call<ResponseBody>, builder: String){
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {
                    val strJson = response.body()!!.string()
                    resultSuccess(strJson)
                } catch (e: Exception) {
                    val msg = if (response.code().toString() != "200") {
                        "msg: " + response.code().toString() + " " + response.message()
                    } else {
                        val errors = StringWriter()
                        e.printStackTrace(PrintWriter(errors))
                        "msg: $errors"
                    }
                    resultError("command: $isApiCode $msg")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                resultError(t.toString())
            }
        })
    }

    private fun resultSuccess(strJson: String) {
        try { if (isLoading) loading.dismiss() } catch (e: Exception) { }
        isLog(isApiCode, strJson)

        // check not null, not isJsonType
        if (strJson.isNotEmpty() && isJsonType(strJson)) {
            isListener.onSuccessResult(strJson, isApiCode)
        }else {
            resultError("api return not json type")
        }
    }

    private fun resultError(strError: String) {
        try { if (isLoading) loading.dismiss() } catch (e: Exception) { }
        isLog("Connect Server Error : ", strError)
        showDialogAlert(context, false, context.getString(R.string.error) + " :\n" + strError)

        try { isListener.onErrorResult(strError, isApiCode) } catch (e: Exception) { }
    }

}
