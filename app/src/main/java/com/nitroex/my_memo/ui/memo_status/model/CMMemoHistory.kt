package com.nitroex.my_memo.ui.memo_status.model

data class CMMemoHistory(
    val command: String,
    val `data`: List<MemoHistory>,
    val message: String
)

data class MemoHistory(
    val approve_date: String,
    val approve_time: String,
    val company_id: String,
    val emp_com_id: String,
    val emp_id: String,
    val emp_name: String,
    val emp_pos_initial: String,
    val issue_date: String,
    val issue_time: String,
    val memo_comment: String,
    val memo_history_id: String,
    val memo_id: String,
    val memo_status_id: String,
    val memo_status_name: String
)