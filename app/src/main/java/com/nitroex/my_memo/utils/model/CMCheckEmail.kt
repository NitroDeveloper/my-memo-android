package com.nitroex.my_memo.utils.model

import java.io.Serializable

data class CMCheckEmail(
    val command: String,
    val `data`: ArrayList<CheckEmail>,
    val message: String
)

data class CheckEmail(
    val company_code: String,
    val company_id: String
): Serializable