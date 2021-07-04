package com.nitroex.my_memo.ui.memo_status.model

data class CMMemoStatus(
    val command: String,
    val `data`: List<MemoStatus>,
    val message: String
)

data class MemoStatus(
    val memo_action_name: String,
    val memo_create_date: String,
    val memo_create_time: String,
    var memo_favorite_status: Int,
    val memo_form_id: Int,
    val memo_from_name: String,
    val memo_id: Int,
    val memo_last_update: String,
    val memo_no: String,
//    val memo_revise: Int,
    val memo_status_id: Int,
    val memo_status_name: String,
    val memo_subject: String,
    val memo_form_name: String,
    val mmh_name: String,
    val mst_status_name: String,
    val show_resent_button: Int,
    val show_view_icon: Int,
    val mm_create_channel: Int
)