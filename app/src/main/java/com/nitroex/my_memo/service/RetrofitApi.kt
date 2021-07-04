package com.nitroex.my_memo.service

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitApi {
    @POST(".")
    fun getResponseBody(@Body body: MultipartBody): Call<ResponseBody>
}