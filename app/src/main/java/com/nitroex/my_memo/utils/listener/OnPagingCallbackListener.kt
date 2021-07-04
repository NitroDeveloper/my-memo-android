package com.nitroex.my_memo.utils.listener

interface OnPagingCallbackListener {
     fun onLoadMore()
     fun onError(msg: String)
}