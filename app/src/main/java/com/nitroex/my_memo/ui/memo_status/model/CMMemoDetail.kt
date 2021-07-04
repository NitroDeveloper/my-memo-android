package com.nitroex.my_memo.ui.memo_status.model

import com.nitroex.my_memo.ui.memo_create.model.ListTo
import com.nitroex.my_memo.utils.attachFile.model.AttachFile

data class CMMemoDetail(
    val command: String,
    val message: String,
    val `data`: ArrayList<MemoDetail>,
    val to_emp: ArrayList<ListTo>,
    val cc_emp: ArrayList<ListTo>,
    val attachfile: ArrayList<AttachFile>,
    val memo_no_info: List<MemoNoInfo>
)

data class MemoDetail(
    val comment_count: String,
    val from_name: String,
    val from_position: String,
    val from_type: String,
    val is_show_to: String,
    val memo_attachment: String,
    val memo_date: String,
    val memo_detail: String,
    val memo_government: String,
    val memo_no: String,
    val memo_show_to: String,
    val memo_subject: String,
    val secret_level: String,
    val show_button_approve: Int,
    val show_button_cancel: Int,
    val show_button_copy: Int,
    val show_button_disapprove: Int,
    val show_button_export: Int,
    val show_button_revise: Int,
    val show_button_edit_file: Int,
    val show_form: Int,
    val urgent_level: String,
    val memo_id: String,
    val memo_status_id: String,
    val memo_form_id: String,
    val memo_signature: String,
    val mm_create_channel: Int,
    val memo_format_lang: String,
    val memo_refer_to: String ,
    val memo_postscript: String,
    val memo_govt_subject_owner: String,
    val memo_tel: String,
    val memo_fax: String,
    val memo_email: String,
    val cc_employee: String
)

data class MemoNoInfo(
    val memo_no_id: String,
    val memo_no_key: String
)
