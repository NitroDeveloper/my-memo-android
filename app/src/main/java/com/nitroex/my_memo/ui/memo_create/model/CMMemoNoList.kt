package com.nitroex.my_memo.ui.memo_create.model

data class CMMemoNoList(
    val command: String,
    val `data`: List<MemoNoList>,
    val message: String
)

data class MemoNoList(
    val mno_format_date: String,
    val mno_format_memoNo: String,
    val mno_id_pk: String,
    val mno_key_name: String,
    val mno_running_no: String,
    val mno_show_date: String,
    val mno_year: String,
    val prelist_concurred: Boolean,
    val show_format: String
)