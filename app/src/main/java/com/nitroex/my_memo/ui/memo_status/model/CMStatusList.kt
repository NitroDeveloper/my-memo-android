package com.nitroex.my_memo.ui.memo_status.model

data class CMStatusList(
    val command: String,
    val `data`: List<StatusList>,
    val message: String
)

data class StatusList(
    val memo_status_id: Int,
    val memo_status_name: String
)