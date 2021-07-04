package com.nitroex.my_memo.utils.model

data class CMProfile(
    val command: String,
    val message: String,
    val notice: List<Notice>,
    val current_documents: List<CurrentDocument>,
    val wait_for_agree_total: Int,
    val wait_for_approve_total: Int
)
data class CurrentDocument(
    val document_total: Int,
    val status_id: Int,
    val status_name: String
)

data class Notice(
    val id: Int,
    val name: String,
    val send: Int
)