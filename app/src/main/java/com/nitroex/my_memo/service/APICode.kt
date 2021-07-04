package com.nitroex.my_memo.service

import android.content.Context
import com.nitroex.my_memo.BaseActivity
import com.nitroex.my_memo.utils.Configs

object APICode {
    lateinit var URL: String
    private var api: APICode? = null

    fun newInstance(context: Context): APICode? {
        if (api == null) api = APICode
        URL = (context as BaseActivity).getSharePreSystem(context, Configs.URL_API, "")+"/"
        return api
    }

    // Get Command Api
    var LOGIN = "0100"
    var LOGOUT = "0200"
    var FORGOT_PASSWORD = "0300"
    var CHECK_OTP_FORGOT_PASSWORD = "0400"
    var SET_LOGOUT = "0500"
    var CHANGE_PASSWORD_BY_FORGOT_PASSWORD = "0600"
    var GET_MY_PROFILE = "1200"
    var GET_DRAFT_LIST = "1300"
    var GET_FORM_LIST = "1500"
    var GET_MEMO_NO_LIST = "1600"
    var GET_APPROVE_LIST = "2000"
    var SAVE_DRAFT_MEMO = "12200"
    var INSERT_MEMO_NEW = "12300"
    var REVISE_MEMO = "12400"
    var INSERT_MEMO_FROM_DRAFT = "12500"
    var GET_MEMO_LIST = "2800"
    var GET_MEMO_DETAIL_NEW = "12900"
    var GET_MEMO_HISTORY = "3000"
    var GET_MEMO_STATUS_LIST = "3100"
    var FAVORITE_MEMO = "3200"
    var RESENT_MEMO = "3300"
    var UPDATE_PROFILE_IMAGE = "4000"
    var DELETE_ATTACH_FILE = "4100"
    var IS_LOGIN = "0104"
    var APPROVE_MEMO = "3700"
    var DISAPPROVE_MEMO = "3800"
    var SET_MEMO_NOTICE = "4200"
    var GET_FAVORITE_LIST = "2600"
    var GET_EXPORT_URL = "3400"
    var GET_TITLE_FORM = "7100"
    var DELETE_DRAFT_MEMO = "7200"
    var CHECK_EMAIL = "7300"
    var CANCEL_MEMO = "8100"
    var UPDATE_ATTACH_FILES = "19200"
    var UPLOAD_MY_SIGNATURE = "9100"
    var GET_MY_SIGNATURE = "9200"
    // Get Command Api

}