package com.nitroex.my_memo.utils.model

data class CMLogin(
    val command: String,
    val message: String,
    val company_id: String,
    val advance_no_fornamt: String,
    val check_notice: Int,
    val `data`: List<Login>,
    val company_info: CompanyInfo,
    val company: ArrayList<Company>,
    val emp_menu: List<EmpMenu>
)

data class Login(
    val com_memo_approve_stamp: String,
    val dp_name: String,
    val emp_com_id: String,
    val emp_company_id: String,
    val emp_dp_id: String,
    val emp_dv_id: String,
    val emp_email: String,
    val emp_id: String,
    val emp_level: String,
    val emp_name: String,
    val emp_phone: String,
    val emp_pos_initial: String,
    val emp_position: String,
    val emp_profile_image: String,
    val emp_sex: String,
    val emp_st_id: String,
    val emp_start_work_date: String,
    val emp_status: String,
    val emp_type_id: String,
    val emp_username: String
)

data class Company(
    val com_doc_logo: String,
    val com_id_pk: String,
    val com_logo: String,
    val company_name: String,
    val dp_id_pk: String,
    val dv_id_pk: String,
    val emp_id_pk: String,
    val emp_com_id: String,
    val emp_menu: List<EmpMenu>?,
    val st_id_pk: String
)

data class CompanyInfo(
    val com_doc_logo: String,
    val com_logo: String,
    val com_memo_noti: String,
    val com_name: String
)

data class EmpMenu(
    val menu_id: Int,
    val menu_name: String
)