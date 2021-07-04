package com.nitroex.my_memo.utils.listener

import java.io.File

interface OnResizeImageListener {
     fun onSuccessResult(file: File, value: String)
     fun onErrorResult(msg: String)
}