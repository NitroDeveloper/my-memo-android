package com.nitroex.my_memo.utils.attachFile.model

import com.nitroex.my_memo.utils.attachFile.adapter.AttachFileAdapter
import java.io.Serializable

class AttachFile(
        var id: Int=0,
        var type_id: Int = AttachFileAdapter.IMAGE,
        var path: String="",
        var file_name: String="",
        var file_type: String="",
        var thumb_path: String="",
        var employee_id: Int=0,
        var company_id: Int=0,
        var attach_info: String="",
): Serializable
