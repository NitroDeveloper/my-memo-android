package com.nitroex.my_memo.utils.listener

interface OnCameraCallbackListener {
     fun onCameraSuccess(isPath: String)
     fun onCameraError(msg: String)
}