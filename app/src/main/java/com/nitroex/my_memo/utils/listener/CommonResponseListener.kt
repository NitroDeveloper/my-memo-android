package com.nitroex.my_memo.utils.listener

interface CommonResponseListener {
     fun onSuccessResult(strJson: String, apiCode: String)
     fun onErrorResult(msg: String, apiCode: String)
}