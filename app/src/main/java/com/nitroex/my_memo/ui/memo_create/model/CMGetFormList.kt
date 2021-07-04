package com.nitroex.my_memo.ui.memo_create.model

data class CMGetFormList(
    val command: String,
    val `data`: List<GetFormList>,
    val defualt_id: Any,
    val message: String
)

data class GetFormList(
    val mf_form_detail: String,
    val mf_form_name: String,
    val mf_id_pk: String
)