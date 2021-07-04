package com.nitroex.my_memo.ui.memo_create.model

import java.io.Serializable

data class CMListTo(
    val command: String,
    val `data`: List<ListTo>,
    val message: String
)

data class ListTo(
    val department_id: Int,
    val division_id: Int,
    val emp_com_id: String,
    val emp_name: String,
    val emp_pos_initial: String,
    val emp_position: String,
    val employee_id: Int,
    val section_id: Int

): Serializable