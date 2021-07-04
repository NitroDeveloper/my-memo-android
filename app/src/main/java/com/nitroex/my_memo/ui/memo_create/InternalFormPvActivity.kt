package com.nitroex.my_memo.ui.memo_create

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.nitroex.my_memo.BaseActivity
import com.nitroex.memo.my_memo.R
import com.nitroex.my_memo.service.APICode
import com.nitroex.my_memo.service.RetrofitClient
import com.nitroex.my_memo.ui.MainActivity
import com.nitroex.my_memo.ui.memo_create.model.CMExportURL
import com.nitroex.my_memo.ui.memo_create.model.ListTo
import com.nitroex.my_memo.ui.memo_draft.DraftMemoActivity
import com.nitroex.my_memo.ui.memo_status.MemoHistoryActivity
import com.nitroex.my_memo.ui.memo_status.MemoStatusActivity
import com.nitroex.my_memo.ui.memo_status.model.CMMemoDetail
import com.nitroex.my_memo.utils.Configs
import com.nitroex.my_memo.utils.DateUtils
import com.nitroex.my_memo.utils.attachFile.AttachFileActivity
import com.nitroex.my_memo.utils.attachFile.GalleryActivity
import com.nitroex.my_memo.utils.attachFile.adapter.AttachFileAdapter
import com.nitroex.my_memo.utils.attachFile.model.AttachFile
import com.nitroex.my_memo.utils.dialog.DialogSignature
import com.nitroex.my_memo.utils.listener.CommonResponseListener
import com.nitroex.my_memo.utils.listener.OnDialogCallbackListener
import com.nitroex.my_memo.utils.listener.OnDialogDismissListener
import com.nitroex.my_memo.utils.model.CMModel
import com.nitroex.my_memo.utils.webview.WebViewManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_internal_form_pv.*
import kotlinx.android.synthetic.main.view_icon_bottom.*
import kotlinx.android.synthetic.main.view_title_head.*
import kotlinx.android.synthetic.main.view_webview_memo_detail.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable

class InternalFormPvActivity : BaseActivity(), CommonResponseListener, DialogSignature.OnInputListener, AttachFileAdapter.OnAttachFileListener {

    // Form Value Send Api
    private var strMemoId = "" //รหัสเอกสาร
    private var strFormId = "" //รหัสฟอร์มเอกสาร
    private var strMemoStatusId = "" //สถานะเอกสาร
    private var strConfidentID = "" //ชั้นความลับ(id)
    private var strConfidentName = "" //ชั้นความลับ(name)
    private var strSpeedLevelID = "" //ชั้นความเร็ว(id)
    private var strSpeedLevelName = "" //ชั้นความลับ(name)
    private var strShowToID = "" //แสดงเรียน

    private var strGovernment = "" //ส่วนราชการ
    private var strDocNoId = "" //ที่(id เลขที่เอกสาร)
    private var strDocNoName = "" //ที่(name เลขที่เอกสาร)
    private var strSubject = "" //เรื่อง
    private var strShowTo = "" //แสดงเรียน
    private var strAttachment = "" //สิ่งที่แนบมาด้วย
    private var strDateApi = "" //วันที่ API
    private var strDateShow = "" //วันที่ Show

    private var strFromId = "0" //จาก(รหัสผู้ส่ง 0,1,2)
    private var strFromName = "" //จาก(ชื่อ)
    private var strFromPosition = "" //จาก(ตำแหน่ง)
    private var strMeDetail = "" //รายละเอียด(สร้างมาจากเว็บ)
    private var strMeReason = "" //เหตุผลที่มีหนังสือไป
    private var strMePurpose  = "" //จุดประสงค์ที่มี
    private var strMeSummary = "" //สรุปวัตถุประสงค์
    private var strMeParagraph = "            " //เว้นวรรณหน้า3ข้อความด้านล่างสุด
    private var strPathSignature = "" //มีค่าเเมื่อใส่ลายเซ็นต์
    private var strCommentCount = "0" //จำนวน history comment
    private var strMemoSignature = ""
    private var strCreateByPlatForm = 0 //สร้าฃจากแพลตฟอร์ม 1=Web, 2=App
    private var isMemoFormatLang = "" //รูปแบบฟอแมท ที่,วันที่ en,th
    private var isFromType = "" //new, copy, revise, draft, favorite

    private var showButtonApprove = 0
    private var showButtonCopy = 0
    private var showButtonExport = 0
    private var showButtonRevise = 0
    private var showButtonCancel = 0
    private var showButtonEditFile = 0

    private var isFromMemoStatus = false
    private var isFromNewExkasan = false
    private var isFromExample = false
    private var isApprove = false //แยกตอนแนบลายเซ็นต์
    private var isDisApprove = false //แยกตอนแนบลายเซ็นต์

    private lateinit var jsonSentTo: JSONObject //เรียน <JsonObject>
    private lateinit var dateUtils: DateUtils
    private lateinit var adapterAttFile: AttachFileAdapter

    private var listToApi: ArrayList<ListTo> = arrayListOf()
    private var listPicFile: ArrayList<AttachFile> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_internal_form_pv)

        getBundle()
        setClick()
    }

    private fun getBundle() {
        val bundle = getBundleFromIntent(this)
        if (bundle != null) {
            setHideFabOnScroll(blockScroll)
            setViewVisible(btnFabIcon,false)
            strMemoId = bundle.getString("memo_id", "")

            when (bundle.getString("isFrom", "")) {
                "NewExkasan" -> {
                    isFromNewExkasan=true
                    setValueFromBundle(bundle)
                }
                "MemoStatus" -> {
                    isFromMemoStatus=true
                    getMemoDetail(strMemoId)
                }
                "MemoExample" -> {
                    isFromExample=true
                    getExampleForm(bundle.getString("memo_form_id", ""))
                }
            }

        }
    }

    private fun setClick() {

        btnBackEdit.setOnClickListener { onBackPressed() }
        btnHeadBack.setOnClickListener { onBackPressed() }

        btnFabIcon.setOnClickListener {
            if (isFromNewExkasan) {
                showDialogConfirm(object : OnDialogCallbackListener {
                    override fun onSubmit() { openActivityFinish(MainActivity::class.java) }
                    override fun onCancel() {}
                }, getString(R.string.want_esc))

            }else{ openActivityFinish(MainActivity::class.java) }
        }
        btnTitleAction.setOnClickListener {
            if (isFromNewExkasan) {
                showDialogConfirm(object : OnDialogCallbackListener {
                    override fun onSubmit() { checkSentFromType(Configs.Draft) }
                    override fun onCancel() {}
                }, getString(R.string.want_back_to_edit))

            }else{
                val bundle = Bundle().apply {
                    putString("memo_id", strMemoId)
                }
                openActivityWithBundle(MemoHistoryActivity::class.java, bundle)
            }
        }
        btnSent.setOnClickListener {
            showDialogConfirm(object : OnDialogCallbackListener {
                override fun onSubmit() {
                    DialogSignature.display(supportFragmentManager, object :
                        OnDialogCallbackListener {
                        override fun onSubmit() {}
                        override fun onCancel() {}
                    }, true)
                }
                override fun onCancel() {}
            }, getString(R.string.want_send))
        }
        btnAddFile.setOnClickListener {
            val bundle = Bundle().apply {
                putString("menuName", getString(R.string.edit_attach_file))
                putString("strFromName", strFromName)
                putString("memo_id", strMemoId)
                putString("strAttachment", strAttachment)
                putSerializable("listPicFile", listPicFile)
            }
            openActivityWithBundleForResult(AttachFileActivity::class.java, bundle, Configs.REQUEST_ATTACH_FILE)
        }
        btnCancel.setOnClickListener {
            showDialogConfirm(object : OnDialogCallbackListener {
                override fun onSubmit() { setCancelMemo() }
                override fun onCancel() {}
            }, getString(R.string.want_cancel))
        }
        btnApprove.setOnClickListener {
            showDialogConfirm(object : OnDialogCallbackListener {
                override fun onSubmit() {
                    isApprove=true
                    DialogSignature.display(supportFragmentManager, object :
                        OnDialogCallbackListener {
                        override fun onSubmit() {}
                        override fun onCancel() {}
                    }, true)
                }
                override fun onCancel() {}
            }, getString(R.string.approve_confirm))
        }
        btnDisapprove.setOnClickListener {
            if (checkFormIsNotEmpty(edtCommentApprove,getString(R.string.please_fill_comment))) {
                showDialogConfirm(object : OnDialogCallbackListener {
                    override fun onSubmit() {
                        isDisApprove=true
                        DialogSignature.display(supportFragmentManager, object :
                            OnDialogCallbackListener {
                            override fun onSubmit() {}
                            override fun onCancel() {}
                        }, true)
                    }
                    override fun onCancel() {}
                }, getString(R.string.disapprove_confirm))
            }
        }
        btnExport.setOnClickListener {
            showDialogConfirm(object : OnDialogCallbackListener {
                override fun onSubmit() { getExportPDF() }
                override fun onCancel() {}
            }, getString(R.string.want_export_pdf))
        }
        btnCopy.setOnClickListener {
            showDialogConfirm(object : OnDialogCallbackListener {
                override fun onSubmit() { setCopyMemo() }
                override fun onCancel() {}
            }, getString(R.string.want_copy))
        }
        btnRevise.setOnClickListener {
            showDialogConfirm(object : OnDialogCallbackListener {
                override fun onSubmit() { setReviseMemo() }
                override fun onCancel() {}
            }, getString(R.string.want_revise))
        }
    }

    private fun setCopyMemo(){
        val bundle = Bundle().apply {
            putString("menuId", strFormId)
            putString("memo_id", strMemoId)
            putString("isFromType", Configs.CopyForm)
        }
        openActivityWithBundle(InternalFormActivity::class.java, bundle)
    }

    private fun setReviseMemo(){
        val bundle = Bundle().apply {
            putString("menuId", strFormId)
            putString("memo_id", strMemoId)
            putString("isFromType", Configs.ReviseForm)
        }
        openActivityWithBundle(InternalFormActivity::class.java, bundle)
    }

    // มาจากหน้าลายเซ็นต์หลังจากคอนเฟิร์มการส่งไปแล้ว
    override fun sendPathPicSignature(isSkip: Boolean, input: String?) {
        DialogSignature.dialogSignature!!.dismiss()
        if (!isSkip) { strPathSignature = input!! }

        if (isFromMemoStatus) { //by: approve, disapprove
            if (isApprove) { setApproveMemo() }
            if (isDisApprove) { setDisapproveMemo() }
        }else{
            checkSentFromType(Configs.Insert)
        }
    }
    private fun checkSentFromType(callType: String) {
        sentMemoApi(callType)
    }

    // Open Dialog Signature onPause App Stop (clear Bundle)
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        when (requestCode) {
            Configs.REQUEST_ATTACH_FILE -> { this.recreate() }
        }
    }

    ////////////////////////////////////////////////////////////// Function Call Api //////////////////////////////////////////////////////////////

    private fun getMemoDetail(memoId: String) {
        val jsonObject = JSONObject().apply {
            put("memo_id", memoId)
        }
        RetrofitClient().newInstance()
            .setNetworkCall(this, this, jsonObject, APICode.URL, APICode.GET_MEMO_DETAIL_NEW, isLocation = false, isLoading = true)
    }

    private fun sentMemoApi(callType: String) {
        val jsonObject = JSONObject()
        var apiCode = ""
        when (callType) {
            Configs.Insert -> {
                when (isFromType) {
                    Configs.NewForm -> {
                        strMemoId = "0"
                        apiCode = APICode.INSERT_MEMO_NEW
                    }
                    Configs.CopyForm -> {
                        strMemoId = "0"
                        apiCode = APICode.INSERT_MEMO_NEW
                    }
                    Configs.FavoriteForm -> {
                        strMemoId = "0"
                        apiCode = APICode.INSERT_MEMO_NEW
                    }
                    Configs.ReviseForm -> {
                        apiCode = APICode.REVISE_MEMO
                    }
                    Configs.DraftForm -> {
                        apiCode = APICode.INSERT_MEMO_FROM_DRAFT
                    }
                }
            }
            Configs.Draft -> {
                apiCode = APICode.SAVE_DRAFT_MEMO
                if (isFromType!= Configs.DraftForm) {
                    strMemoId = "0"
                }
            }
        }

        jsonObject.apply {
            put("memo_id", strMemoId)
            put("memo_form_id", strFormId)
            put("secret_level", strConfidentID)
            put("urgent_level", strSpeedLevelID)
            put("memo_government", strGovernment)
            put("memo_no_id", strDocNoId)
            put("memo_date", strDateApi)
            put("memo_subject", strSubject)
            put("to_employee", jsonSentTo)
            put("is_show_to", strShowToID)
            put("memo_show_to", strShowTo)
            put("memo_attachment", setTextNToBr(strAttachment))
            put("from_name", strFromName)
            put("from_type", strFromId)
            put("from_position", setTextNToBr(strFromPosition))
            put("mm_create_channel", strCreateByPlatForm)
            put("memo_format_lang", isMemoFormatLang)


            when (strCreateByPlatForm) {
                Configs.Channel_ID_Mobile -> {
                    put("memo_detail", setTextNToBr(getParagraphArrayToString(listOf(strMeReason, strMePurpose, strMeSummary))))
                }
                Configs.Channel_ID_Web -> {
                    put("memo_detail", strMeDetail)
                }
            }

            var fileSize = 0
            for (i in listPicFile.indices){
                if (listPicFile[i].id==0) {
                    put("attach_file_$fileSize", listPicFile[i].path) //file
                    fileSize++
                }
            }
            put("attach_file_size", fileSize)

            if (strPathSignature.isNotEmpty()) {
                put("attach_file_signature", strPathSignature) //file
            }
        }
        RetrofitClient().newInstance()
            .setNetworkCall(this, this, jsonObject, APICode.URL, apiCode, isLocation = false, isLoading = true)
    }

    private fun setCancelMemo() {
        val jsonObject = JSONObject().apply {
            put("memo_id", strMemoId)
            put("memo_comment", "")
        }
        RetrofitClient().newInstance()
            .setNetworkCall(this, this, jsonObject, APICode.URL, APICode.CANCEL_MEMO, isLocation = false, isLoading = true)
    }
    private fun setApproveMemo(){
        val jsonObject = JSONObject().apply {
            put("memo_id", strMemoId)
            put("memo_comment", setTextNToBr(getTextToTrim(edtCommentApprove)))
            put("memo_form_id", strFormId)
            put("attach_file_signature", strPathSignature) //file
        }
        RetrofitClient().newInstance()
            .setNetworkCall(this, this, jsonObject, APICode.URL, APICode.APPROVE_MEMO, isLocation = false, isLoading = true)
    }
    private fun setDisapproveMemo(){
        val jsonObject = JSONObject().apply {
            put("memo_id", strMemoId)
            put("memo_comment", setTextNToBr(getTextToTrim(edtCommentApprove)))
            put("memo_form_id", strFormId)
            put("attach_file_signature", strPathSignature) //file
        }
        RetrofitClient().newInstance()
            .setNetworkCall(this, this, jsonObject, APICode.URL, APICode.DISAPPROVE_MEMO, isLocation = false, isLoading = true)
    }
    private fun getExportPDF(){
        val jsonObject = JSONObject().apply {
            put("memo_id", strMemoId)
        }
        RetrofitClient().newInstance()
            .setNetworkCall(this, this, jsonObject, APICode.URL, APICode.GET_EXPORT_URL, isLocation = false, isLoading = true)
    }
    private fun getExampleForm(formId: String) {
        val jsonObject = JSONObject().apply {
            put("memo_form_id", formId)
        }
        RetrofitClient().newInstance()
            .setNetworkCall(this, this, jsonObject, APICode.URL, APICode.GET_TITLE_FORM, isLocation = false, isLoading = true)
    }

    override fun onSuccessResult(strJson: String, apiCode: String) {

        if (apiCode in RetrofitClient().apiSentMemo) {
            val model = Gson().fromJson(strJson, CMModel::class.java)
            when (model.command) {
                apiCode -> {
                    showDialogAlertListener(true, model.message,
                        object : OnDialogDismissListener {
                            override fun onDismiss() {
                                val bundle = Bundle().apply {
                                    putString("menuName", getString(R.string.memo_status))
                                    putInt("menuAction", Configs.REQUEST_MENU_STATUS_MEMO)
                                }
                                if (apiCode == APICode.SAVE_DRAFT_MEMO) { //Insert Draft Memo
                                    openActivityFinishWithBundle(DraftMemoActivity::class.java, bundle)
                                }else{ //Insert New Memo
                                    openActivityFinishWithBundle(MemoStatusActivity::class.java, bundle)
                                }
                            }
                        })
                }
                else -> showDialogAlert(false, model.message)
            }
        }else{

            when (apiCode) {
                APICode.GET_MEMO_DETAIL_NEW -> {
                    val model = Gson().fromJson(strJson, CMMemoDetail::class.java)
                    when (model.command) {
                        apiCode -> { setValueFromApi(model) }
                        else -> showDialogAlert(false, model.message)
                    }
                }
                APICode.GET_EXPORT_URL -> {
                    val model = Gson().fromJson(strJson, CMExportURL::class.java)
                    when (model.command) {
                        apiCode -> {
                            try { setIntentViewExport(model.export_url)
                            } catch (e: Exception) {showDialogAlert(false, e.message) }
                        }
                        else -> showDialogAlert(false, model.message)
                    }
                }
                APICode.CANCEL_MEMO, APICode.APPROVE_MEMO, APICode.DISAPPROVE_MEMO -> {
                    val model = Gson().fromJson(strJson, CMModel::class.java)
                    when (model.command) {
                        apiCode -> { showDialogReturnRefreshActivity(strJson) }
                        else -> showDialogAlert(false, model.message)
                    }
                }
                APICode.GET_TITLE_FORM -> {
                    val model = Gson().fromJson(strJson, CMMemoDetail::class.java)
                    when (model.command) {
                        apiCode -> { setValueFromApi(model) }
                        else -> showDialogAlert(false, model.message)
                    }
                }
            }
        }
    }
    override fun onErrorResult(msg: String, apiCode: String) {}

    ////////////////////////////////////////////////////////////// Function Call Api //////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////// Function Set View //////////////////////////////////////////////////////////////

    @SuppressLint("SetTextI18n")
    private fun setValueFromBundle(bundle: Bundle) {
        bundle.apply {
            getString("isFromType", "").let{ isFromType=it } //new, copy, revise, draft
            getString("strFormId","").let{ strFormId=it }
            getString("strConfidentID", "").let { strConfidentID=it }
            getString("strSpeedLevelID", "").let { strSpeedLevelID=it }
            getString("strShowToID", "").let { strShowToID=it }
            getString("strDateApi", "").let { strDateApi=it }
            getString("strDateShow","").let { strDateShow=it }
            getString("strGovernment", "").let { strGovernment=it }
            getString("strDocNoId", "").let { strDocNoId=it }
            getString("strDocNoName", "").let { strDocNoName=it }
            getString("strSubject", "").let { strSubject=it }
            getString("strShowTo", "").let {  strShowTo=it }
            getString("strAttachment", "").let { strAttachment=it }
            getString("strMeReason", "").let { strMeReason=it }
            getString("strMePurpose", "").let { strMePurpose=it }
            getString("strMeSummary", "").let { strMeSummary=it }
            getString("strFromID", "").let { strFromId=it }
            getString("strFromPosition", "").let { strFromPosition=it }
            getString("strConfidentName", "").let { strConfidentName=it }
            getString("strSpeedLevelName", "").let { strSpeedLevelName=it }
            getString("strFromName", "").let{ strFromName=it }
            getString("strCreateByPlatForm","").let { strCreateByPlatForm=it.toInt() }
            getString("strMeDetail","").let { strMeDetail=it }
            getString("memo_format_lang","").let { isMemoFormatLang=it }

            listToApi = bundle.getSerializable("listToApi") as ArrayList<ListTo>
            listPicFile = bundle.getSerializable("listPicFile") as ArrayList<AttachFile>

            if (listPicFile.isNotEmpty()) { //ลบตำแหน่งที่เป็นรูปปุ่ม +
                if (listPicFile[listPicFile.size-1].type_id== AttachFileAdapter.ADD) {
                    listPicFile.removeAt(listPicFile.size-1)
                }
            }

            //set button title right
            if (isFromType!= Configs.ReviseForm) {
                btnTitleAction.setImageResource(R.drawable.draftmemo)
                setViewVisible(btnTitleAction, true)
            }

            if (isFromType== Configs.DraftForm) {
                bundle.getString("memo_id", "").let{ strMemoId=it }
            }
        }

        setView()
    }

    private fun setValueFromApi(model: CMMemoDetail?) {
        val mData = model!!.data[0]
        mData.apply {
            strConfidentID = secret_level
            strSpeedLevelID = urgent_level
            isMemoFormatLang =  memo_format_lang
            strDateApi =  memo_date
            strGovernment = memo_government

            strDocNoId = model.memo_no_info[0].memo_no_id
            strDocNoName = memo_no

            strSubject = memo_subject
            strShowToID = is_show_to
            strShowTo = memo_show_to
            strAttachment = setTextBrToN(memo_attachment)

            mm_create_channel.also {
                strCreateByPlatForm = it
                when (strCreateByPlatForm) {
                    Configs.Channel_ID_Mobile -> {
                        val listDetail = setClearParagraphStringToArray(setTextBrToN(memo_detail))
                        strMeReason = listDetail[0]
                        strMePurpose = listDetail[1]
                        strMeSummary = listDetail[2]
                    }
                    Configs.Channel_ID_Web -> {
                        strMeDetail = getTextToTrim(memo_detail)
                    }
                }
            }

            showButtonApprove = show_button_approve
            showButtonCopy = show_button_copy
            showButtonExport = show_button_export
            showButtonRevise = show_button_revise
            showButtonCancel = show_button_cancel
            showButtonEditFile = show_button_edit_file

            strConfidentName = getConfidentNameFromId(strConfidentID)
            strSpeedLevelName = getSpeedLevelNameFromId(strSpeedLevelID)

            strFromId = from_type
            strFromName = from_name
            strFromPosition = setTextBrToN(from_position)
            strFormId = memo_form_id
            strMemoStatusId = memo_status_id
//            strCommentCount = comment_count
            strMemoSignature = memo_signature
        }
        listToApi = model.to_emp
        listPicFile = model.attachfile

        setView()
    }

    @SuppressLint("SetTextI18n")
    private fun setView() {
        dateUtils = DateUtils().newInstance(); dateUtils.setEasyDate(this)

        if (isFromExample) {
            tvHeadTitle.text = getString(R.string.example)  //ไตเติ้ลแถบด้านบน(ชื่อฟอร์มเอกสาร)
            strDateShow = dateUtils.convertDateApiToShow(strDateApi,true)
        }else{
            setTitleFormById(tvHeadTitle, strFormId)  //ไตเติ้ลแถบด้านบน(ชื่อฟอร์มเอกสาร)
            strDateShow = dateUtils.convertDateApiByLang(strDateApi, isMemoFormatLang)
        }

        setTitleFormById(tvHeadTitleForm, strFormId) //ไตเติ้ลบนหัวเอกสาร(ชื่อฟอร์มเอกสาร)
        tvDate.text = strDateShow
        tvGovernment.text = strGovernment
        tvDocTo.text = strDocNoName
        tvSubject.text = strSubject
        tvAttachment.text = strAttachment

        if (strMeDetail.isNotEmpty() && strCreateByPlatForm== Configs.Channel_ID_Web) { //create from web only
            setViewVisible(blockDetailByWeb,true)
            WebViewManager().showMemoDetail(webViewMemoDetail, strMeDetail)
        }else{
            setViewVisible(blockDetailByMobile,true)
            tvMeReason.text = "$strMeParagraph$strMeReason"
            tvMePurpose.text = "$strMeParagraph$strMePurpose"
            tvMeSummary.text = "$strMeParagraph$strMeSummary"
        }

        tvFromPosition.text = strFromPosition
        tvFromName.text = strFromName

        takeIf { strConfidentID!="0" }?.apply { tvConfidential.text = strConfidentName }
        takeIf { strSpeedLevelID!="0" }?.apply { tvSpeedLevel.text = strSpeedLevelName }

        //โชว์เรียน
        jsonSentTo = getToJsonObjectFromList(listToApi)
        val dummyToApi = listToApi
        if (isFromNewExkasan) { dummyToApi.reverse() }
        if (strShowToID=="0")
            tvFormTo.text = getToPreviewFromList(dummyToApi)
        else tvFormTo.text = strShowTo

        btnFileCount.text = "(${listPicFile.size})"
        setAttachFileList(listPicFile)
        if (strAttachment.isEmpty()) { setViewVisible(blockAttachment,false) }

        // Set Show Block Button
        if (showButtonApprove==1) setViewVisible(blockApproveAndComment,true)
        if (showButtonCopy==1) setViewVisible(btnCopy,true)
        if (showButtonExport==1) setViewVisible(btnExport,true)
        if (showButtonRevise==1) setViewVisible(btnRevise,true)
        if (showButtonCancel==1) setViewVisible(btnCancel,true)
        if (showButtonEditFile==1) setViewVisible(btnAddFile,true)

        if (isFromNewExkasan) { //เอกสารใหม่
            setViewVisible(blockBtnEditAndSent,true)
            if (listPicFile.isEmpty()) { setViewVisible(blockEditFile,false) //ปิดทั้งบล๊อกเมื่อไม่มีไฟล์แนบ
            }else{ setViewVisible(btnAddFile,false) } //ปิดเฉพาะปุ่ม+เพิ่มไฟล์
        }
        if (isFromMemoStatus) { //เอกสารที่สร้างแล้ว
            //ตั้งค่าปุ่มบนไตเติ้ลด้านขวา
            setViewVisible(btnTitleAction, true)
            if (strCommentCount.toInt() > 0) {
                btnTitleAction.setImageResource(R.drawable.history_red)
            }else{ btnTitleAction.setImageResource(R.drawable.history) }

//            val status = listOf(Configs.APPROVE, Configs.DISAPPROVE)
//            if (strMemoStatusId.toInt() in status) {
//                setViewVisible(blockEditFile, false)
//            }
            if (listPicFile.isEmpty() && showButtonEditFile==1) {
                setViewVisible(blockEditFile, true)
            }else{
                if (listPicFile.isEmpty()) {
                    setViewVisible(blockEditFile, false)
                }
            }
            if (checkTextIsNotEmpty(strMemoSignature)) {
                setViewVisible(ivSignature,true)
                setSignatureByBase64(strMemoSignature, ivSignature!!)
            }
        }
        if (isFromExample){ //ตัวอย่างแบบฟอร์ม
            if (listPicFile.isEmpty()) { setViewVisible(blockEditFile,false) //ปิดทั้งบล๊อกเมื่อไม่มีไฟล์แนบ
            }else{ setViewVisible(btnAddFile,false) } //ปิดเฉพาะปุ่ม+เพิ่มไฟล์
        }

        setViewVisible(blockScroll,true) //โชว์บล๊อกทั้งหมดเมื่อเซ็ตค่าเสร็จ
        setViewVisible(btnFabIcon,true)
    }

    private fun setAttachFileList(listPicFile: ArrayList<AttachFile>) {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapterAttFile = AttachFileAdapter(this, true)
        adapterAttFile.setOnAttachFileListener(this)
        rvAttachFile.layoutManager = layoutManager
        rvAttachFile.adapter = adapterAttFile
        adapterAttFile.setItem(listPicFile)
    }
    override fun onImageClick(url: String?, ivImage: ImageView, position: Int) {
        val intent = Intent(this, GalleryActivity::class.java)
        intent.putExtra("gallery_list", listPicFile as Serializable)
        intent.putExtra("index_select", position)
        startActivity(intent)
    }
    override fun onAttachClick() {}
    override fun onRemoveClick(position: Int) {}

    ////////////////////////////////////////////////////////////// Function Set View //////////////////////////////////////////////////////////////

    /////////////////////////////////////// convert data show and sent to api //

    private fun getToPreviewFromList(listToApi: ArrayList<ListTo>): String {
        var txt = ""
        for (i in listToApi.indices){
            txt += listToApi[i].emp_name
            if (listToApi.size!=i+1) { txt +="\n" } //set no last index
        }
        return txt
    }

    private fun getToJsonObjectFromList(listToApi: ArrayList<ListTo>): JSONObject {
        val jsonObj = JSONObject()
        val jsonArr = JSONArray()

        for (i in listToApi.indices){
            val list1 = JSONObject()
            list1.put("to_emp_com_id", listToApi[i].emp_com_id)
            list1.put("to_emp_pos_initial", listToApi[i].emp_pos_initial)
            jsonArr.put(list1)
            jsonObj.put("to_emp", jsonArr)
        }

        return jsonObj
    }

    /////////////////////////////////////// convert data show and sent to api //
}