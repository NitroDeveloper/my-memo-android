package com.nitroex.my_memo.ui.memo_draft.model

data class CMDraftMemo(
    val command: String,
    val `data`: List<DraftMemo>,
    val message: String
)

data class DraftMemo(
    val memo_create_date: String,
    val memo_create_time: String,
    val memo_form_id: String,
    val memo_from_name: String,
    val memo_id: String,
    val memo_subject: String,
    val memo_form_name: String
)