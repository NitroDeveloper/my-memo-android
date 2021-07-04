package com.nitroex.my_memo.ui.memo_create.model

data class CMConclusion(
    val command: String,
    val `data`: List<Conclusion>,
    val message: String
)

data class Conclusion(
    val ccs_id: Int,
    val ccs_name: String
)