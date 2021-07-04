package com.nitroex.my_memo.ui.memo_create

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.nitroex.my_memo.BaseActivity
import com.nitroex.memo.my_memo.R
import com.nitroex.my_memo.service.APICode
import com.nitroex.my_memo.service.RetrofitClient
import com.nitroex.my_memo.ui.MainActivity
import com.nitroex.my_memo.ui.memo_create.adapter.ConclusionAdapter
import com.nitroex.my_memo.ui.memo_create.adapter.FormListToAdapter
import com.nitroex.my_memo.ui.memo_create.adapter.MemoNoListAdapter
import com.nitroex.my_memo.ui.memo_create.model.*
import com.nitroex.my_memo.ui.memo_status.model.CMMemoDetail
import com.nitroex.my_memo.utils.*
import com.nitroex.my_memo.utils.FileUtils.clearImageCache
import com.nitroex.my_memo.utils.attachFile.adapter.AttachFileAdapter
import com.nitroex.my_memo.utils.attachFile.GalleryActivity
import com.nitroex.my_memo.utils.attachFile.model.AttachFile
import com.nitroex.my_memo.utils.dialog.DialogListTo
import com.nitroex.my_memo.utils.listener.CommonResponseListener
import com.nitroex.my_memo.utils.listener.OnCameraCallbackListener
import com.nitroex.my_memo.utils.listener.OnDialogCallbackListener
import com.nitroex.my_memo.utils.listener.OnLangChangeListener
import com.nitroex.my_memo.utils.model.CMModel
import com.nitroex.my_memo.utils.webview.WebViewManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_internal_form.*
import kotlinx.android.synthetic.main.dialog_bottom_sheet.view.*
import kotlinx.android.synthetic.main.view_icon_bottom.*
import kotlinx.android.synthetic.main.view_title_head.*
import kotlinx.android.synthetic.main.view_webview_memo_detail.*
import org.jetbrains.anko.toast
import org.json.JSONObject
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class InternalFormActivity : BaseActivity(),
    CommonResponseListener,
        AttachFileAdapter.OnAttachFileListener,
        DialogListTo.OnCallbackListener,
        FormListToAdapter.OnClickListListener,
        ConclusionAdapter.OnClickListListener,
        MemoNoListAdapter.OnClickListListener{

    // Utils
    private lateinit var dateUtils: DateUtils
    private lateinit var radioUtils: RadioBtnUtils
    private lateinit var cameraUtils: CameraUtils
    // Dialog
    private lateinit var dialogDate: DatePickerDialog
    private lateinit var dialogTo: DialogListTo
    private lateinit var dialogConclusion: BottomSheetDialog
    private lateinit var dialogMemoNoList: BottomSheetDialog
    // Adapter
    private lateinit var adapterTo: FormListToAdapter
    private lateinit var adapterMemoNo: MemoNoListAdapter
    private lateinit var adapterConclus: ConclusionAdapter
    private lateinit var adapterAttFile: AttachFileAdapter
    // Model
    private var listToApi: ArrayList<ListTo> = arrayListOf()
    private var listTo: List<ListTo> = listOf()
    private var listPicFile: ArrayList<AttachFile> = arrayListOf()
    private lateinit var modelFileTemp: AttachFile
    // Value
    private var isFromType = "" //new, copy, revise, draft
    private val numMaxFile = Configs.AttachMaxFile
    private var isFromEditor = false
    private var isFromNew = false
    private var removeIndexTemp = 0

    // Value Api
    private var strFromID = "" //จาก(id: 0,1)
    private var strFromName = "" //จาก(name)
    private var strDateApi = "" //วันที่
    private var strFormId = "" //รหัสฟอร์มเอกสาร
    private var strMemoId = "" //รหัสเอกสาร(กรณีแก้ไข)
    private var strDocNoId = "" //ที่(id เลขที่เอกสาร)
    private var strMeDetail = "" //รายละเอียด(สร้างมาจากเว็บ)
    private var strCreateByPlatForm = 0 //สร้าฃจากแพลตฟอร์ม 1=Web, 2=App
    private var isMemoFormatLang = "" //รูปแบบฟอแมท ที่,วันที่ en,th

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_internal_form)

        setView()
        setClick()
    }

    private fun setView() {
        val bundle = getBundleFromIntent(this)
        if (bundle != null) {
            setHideFabOnScroll(blockScroll)
            clearImageCache(this)

            btnTitleAction.setImageResource(R.drawable.formexample)
            setViewVisible(btnTitleAction, true)

            isFromType = bundle.getString("isFromType","") // from_type = new, copy, revise, draft
            strFormId = bundle.getString("menuId","") // memo_form_id
            strMemoId = bundle.getString("memo_id","") // memo_id (ใช้ในกรณีแก้ไข)
            setTitleFormById(tvHeadTitle, strFormId) //ไตเติ้ลแถบด้านบน(ชื่อฟอร์มเอกสาร)
            setTitleFormById(tvHeadTitleForm, strFormId) //ไตเติ้ลบนหัวเอกสาร(ชื่อฟอร์มเอกสาร)

            dateUtils = DateUtils().newInstance(); dateUtils.setEasyDate(this)
            radioUtils = RadioBtnUtils().newInstance()
            radioUtils.setView(this)
            radioUtils.setViewConfidential()
            radioUtils.setViewSpeedLevel()
            radioUtils.setViewShowTo()
            radioUtils.setViewShowFrom(getEmpName())

            when (isFromType== Configs.NewForm) {
                true -> { isFromNew=true }
                false -> { isFromEditor=true }
            }

            getApproveList()
            setRecyclerListTo()
            setAttachFileList()

            setConclusionDialog()

            if (isFromNew) { setViewNewForm()
            }else{ setViewEditForm() }
        }
    }

    private fun setViewNewForm() {
        //default value
        radioUtils.setRadioConfident(0)
        radioUtils.setRadioSpeedLevel(0)
        radioUtils.setRadioShowTo(0)
        radioUtils.setRadioShowFrom(1)
        radioUtils.setRadioShowLanguage(1)
        dateUtils.isSelectLang = radioUtils.isSelectLang

        tvSentFrom.text = getEmpName()
        edtFromPosition.setText(getEmpPosition())
        setViewVisible(blockDetailByMobile, true)
        strCreateByPlatForm = Configs.Channel_ID_Mobile

        dialogDate = dateUtils.getDateDialog(dateUtils.getCalendarNow())
        tvTitleDate.text = dateUtils.getDateNow(isShow = true, isBaseLang = true)
        edtDate.text = dateUtils.getDateNow(isShow = true, isBaseLang = false)
        strDateApi = dateUtils.getDateNow(isShow = false, isBaseLang = false)

        getMemoNoList()
    }

    private fun setViewEditForm() {
        getMemoDetail(strMemoId)
    }

    private fun setValueFromApi(model: CMMemoDetail?) {
        tvTitleDate.text = dateUtils.getDateNow(isShow = true, isBaseLang = true)

        val mData = model!!.data[0]
        val mFile = model.attachfile
        val mToEmp = model.to_emp
        mToEmp.reverse()
        mData.apply {

            ///////// set value
            dialogDate = dateUtils.getDateDialog(dateUtils.getCalendarApi(memo_date))
            radioUtils.setRadioConfident(secret_level.toInt())
            radioUtils.setRadioSpeedLevel(urgent_level.toInt())
            radioUtils.setRadioShowTo(is_show_to.toInt())

            when (memo_format_lang) {
                Configs.Thai-> { radioUtils.setRadioShowLanguage(1) }
                Configs.English -> { radioUtils.setRadioShowLanguage(0) }
            }
            dateUtils.isSelectLang = radioUtils.isSelectLang

            getMemoNoList()
            edtDate.text = dateUtils.convertDateApiToShow(memo_date,false)

            edtGovernment.setText(memo_government)
            edtSubject.setText(memo_subject)

            strDocNoId = model.memo_no_info[0].memo_no_id
            tvDocNo.text = model.memo_no_info[0].memo_no_key

            isMemoFormatLang = memo_format_lang
            strFromID = from_type
            strDateApi = memo_date

            ///////// set condition
            // เซ็ตค่า: ช่องกรอก"แสดงเรียน"
            if (is_show_to.toInt()!=0) { edtShowTo.setText(memo_show_to) }

            // เซ็ตค่า: ช่องกรอก"แสดงจาก"
            if (strFromID.toInt()!=0) {
                //เซ็ตค่า: จาก(ผู้ส่ง)
                if (getTextToTrim(from_name).isNotEmpty()) {
                    edtFromName.setText(from_name)
                }else{
                    edtFromName.setText(getEmpName())
                }
            }else{
                radioUtils.setRadioShowFrom(0)
            }
            edtFromPosition.setText(setTextBrToN(from_position))

            tvSentFrom.text = getEmpName()
            strFromName = getTextToTrim(tvSentFrom)

            mm_create_channel.also {
                strCreateByPlatForm = it
                when (strCreateByPlatForm) {
                    Configs.Channel_ID_Mobile -> {
                        setViewVisible(blockDetailByMobile,true)
                        val meDetail = setClearParagraphStringToArray(setTextBrToN(memo_detail))
                        edtMeReason.setText(meDetail[0])
                        edtMePurpose.setText(meDetail[1])
                        edtMeSummary.setText(meDetail[2])
                    }
                    Configs.Channel_ID_Web -> {
                        showDialogWarningAlert(getString(R.string.please_edit_memo_detail_in_web))
                        setViewVisible(blockDetailByWeb,true)
                        strMeDetail = getTextToTrim(memo_detail)
                        WebViewManager().showMemoDetail(webViewMemoDetail, strMeDetail)
                    }
                }
            }

            // ดึงไฟล์แนบมาเเฉพาะ revise หรือ draft เท่านั้น
            if (isFromType== Configs.ReviseForm || isFromType== Configs.DraftForm) {
                for (i in mFile.indices) {
                    modelFileTemp = mFile[i]
                    checkFileToList(isPlus = false, isModel = true, isPath = "")
                    edtAttachment.setText(setTextBrToN(memo_attachment))
                }
            }

            listToApi.addAll(mToEmp)
            adapterTo.setData(listToApi)

        }
    }

    @SuppressLint("SetTextI18n")
    private fun setClick() {
        btnHeadBack.setOnClickListener { onBackPressed() }
        btnFabIcon.setOnClickListener {
            showDialogConfirm(object : OnDialogCallbackListener {
                override fun onSubmit() { openActivityFinish(MainActivity::class.java) }; override fun onCancel() {}
            }, getString(R.string.want_esc))
        }
        btnTitleAction.setOnClickListener {
            val bundle = Bundle().apply {
                putString("memo_form_id", strFormId)
                putString("isFrom", "MemoExample")
            }
            openActivityWithBundle(InternalFormPvActivity::class.java, bundle)
        }
//        btnBlockDate.setOnClickListener { //old func date
//            dialogDate.show()
//            dialogDate.setOnDateSetListener { _, year, month, day ->
//                val date: Date = dateUtils.convertDateByPickerDialog(year, month, day)
//                edtDate.text = dateUtils.getDateFormatShow(date)
//                strDateApi = dateUtils.getDateFormatApi(date)
//            }
//        }
        edtDate.setOnClickListener {
            dialogDate.show()
            dialogDate.setOnDateSetListener { _,year, month, day ->
                val date: Date = dateUtils.convertDateByPickerDialog(year, month, day)
                edtDate.text = dateUtils.getDateFormatShow(date)
                strDateApi = dateUtils.getDateFormatApi(date)
            }

        }
        btnAddTo.setOnClickListener {
            dialogTo = DialogListTo().setView(getString(R.string.search_to), supportFragmentManager)!!
            Handler().postDelayed({ dialogTo.setRvList(listTo) }, 100)
        }
        btnDocNo.setOnClickListener {
            dialogMemoNoList.show()
        }
        btnConclusion.setOnClickListener {
            dialogConclusion.show()
        }
        btnConfirm.setOnClickListener { checkFormBeforeSent() }
        btnTipsAttachment.setOnClickListener { showDialogTipsAttachment() }

        radioUtils.setDisplayFormLangChange(object : OnLangChangeListener {
            override fun onChange(lang: String) {
//                  strDocNoId = ""
//                  tvDocNo.text = ""
                dateUtils.isSelectLang = lang
                tvDocNo.text = dateUtils.covertDocNoToFormatThai(tvDocNo.text.toString(), radioUtils.isSelectLang)
                edtDate.text = dateUtils.convertDateApiToShow(strDateApi,false)

                getMemoNoList()
            }
        })
    }

    override fun onBackPressed() {
        showDialogConfirm(object : OnDialogCallbackListener {
            override fun onSubmit() { finish() }
            override fun onCancel() {}
        }, getString(R.string.want_esc))
    }

    //////////////////////////////////// Function Dialog

    @SuppressLint("InflateParams")
    private fun setMemoNoListDialog(model: List<MemoNoList>) {
        dialogMemoNoList = BottomSheetDialog(this)
        val v = layoutInflater.inflate(R.layout.dialog_bottom_sheet,null)
        dialogMemoNoList.setContentView(v)

        adapterMemoNo = MemoNoListAdapter(this)
        adapterMemoNo.setOnClickListener(this)
        v.rvList.layoutManager = LinearLayoutManager(this)
        v.rvList.adapter = adapterMemoNo
        v.tvTitle.text = getString(R.string.please_select_doc_no)
        v.btnClose.setOnClickListener { dialogMemoNoList.dismiss() }
        adapterMemoNo.setData(model)
    }
    override fun onClickMemoNoList(model: MemoNoList, position: Int) {
        dialogMemoNoList.dismiss()
        strDocNoId = model.mno_id_pk
        tvDocNo.text = model.mno_key_name
    }

    @SuppressLint("InflateParams")
    private fun setConclusionDialog() {
        dialogConclusion = BottomSheetDialog(this)
        val v = layoutInflater.inflate(R.layout.dialog_bottom_sheet,null)
        dialogConclusion.setContentView(v)

        adapterConclus = ConclusionAdapter(this)
        adapterConclus.setOnClickListener(this)
        v.rvList.layoutManager = LinearLayoutManager(this)
        v.rvList.adapter = adapterConclus
        v.tvTitle.text = getString(R.string.please_select_conclusion)
        v.btnClose.setOnClickListener { dialogConclusion.dismiss() }

        adapterConclus.setData(listOf(Conclusion(1,"จึงเรียนมาเพื่อโปรดทราบ"), Conclusion(2,"จึงเรียนมาเพื่อโปรดทราบและถือปฏิบัติต่อไป" )))
    }
    override fun onClickConclusion(model: Conclusion) {
        dialogConclusion.dismiss()
        tvConclusion.text = model.ccs_name
    }

    //////////////////////////////////// Function Dialog


    //////////////////////////////////// Function List To

    private fun setRecyclerListTo() {
        adapterTo = FormListToAdapter(this)
        adapterTo.setOnClickListener(this)
        rvListTo.layoutManager = LinearLayoutManager(this)
        rvListTo.adapter = adapterTo
    }
    override fun onAddListTo(model: ListTo?) {
        if (!checkDuplicateListTo(model)) { // เช็คบุคคลซ้ำกับที่เลือกหรือไม่
            listToApi.add(model!!)
            adapterTo.setData(listToApi)
        }else{
            toast(R.string.please_select)
        }
    }

    override fun onDeleteListTo(model: ListTo, position: Int) {
        listToApi.removeAt(position)
        adapterTo.setData(listToApi)
    }

    private fun checkDuplicateListTo(model: ListTo?): Boolean {
        for(i in listToApi.indices)
            if (listToApi[i].emp_com_id==model!!.emp_com_id) { return true }
        return false
    }

    //////////////////////////////////// Function List To


    //////////////////////////////////// Function Attach File

    private fun setAttachFileList() {
        cameraUtils = CameraUtils().newInstance()
        cameraUtils.setEasyImage(this)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapterAttFile = AttachFileAdapter(this, false)
        adapterAttFile.setOnAttachFileListener(this)
        rvAttachFile.layoutManager = layoutManager
        rvAttachFile.adapter = adapterAttFile

        if (listPicFile.isEmpty()) checkFileToList(isPlus = true, isModel = false, isPath = "")
    }
    private fun checkFileToList(isPlus: Boolean, isModel: Boolean, isPath: String){
        if (isPlus) {
            if(!FileUtils.isButtonAddFile(listPicFile, numMaxFile))//เพิ่มปุ่มบวกเมื่อยังไม่ MaxFile
                listPicFile.add(AttachFile(0, AttachFileAdapter.ADD,"","","","",0,0,""))
        }else{
            listPicFile = if (isModel) {
                FileUtils.setPathToModel(modelFileTemp, listPicFile, numMaxFile) //เพิ่มทั้ง model ที่มาจาก api
            }else{
                FileUtils.setPathToModel(isPath, listPicFile, numMaxFile) //ไฟล์ที่พึ่งเพิ่มาใหม่จากเครื่อง
            }
        }
        adapterAttFile.setItem(listPicFile)
        tvFileNumber.text = FileUtils.isListSize(listPicFile).toString()
    }

    override fun onAttachClick() { //Select Add Picture
        cameraUtils.showDialogSelectPic(object : OnCameraCallbackListener {
            override fun onCameraSuccess(isPath: String) { checkFileToList(isPlus = false, isModel = false, isPath = isPath) }
            override fun onCameraError(msg: String) { showDialogAlert(false, msg) }
        })
    }

    override fun onRemoveClick(position: Int) {
        if (listPicFile[position].id!=0) { //ลบไฟล์จากระบบ
            showDialogConfirm(object : OnDialogCallbackListener {
                override fun onSubmit() {
                    removeIndexTemp = position
                    deleteAttachFile(strMemoId, listPicFile[position].id.toString())
                }
                override fun onCancel() {}
            }, getString(R.string.want_delete))

        }else{ //ลบไฟล์ที่เพิ่มมาใหม่
            listPicFile.removeAt(position)
            checkFileToList(isPlus = true, isModel = false, isPath = "")
        }
    }

    override fun onImageClick(url: String?, ivImage: ImageView, position: Int) {
        val intent = Intent(this, GalleryActivity::class.java)
        intent.putExtra("gallery_list", listPicFile as Serializable)
        intent.putExtra("index_select", position)
        startActivity(intent)
    }

    //////////////////////////////////// Function Attach File

    ////////////////////////////////////////////////////////////// Function Call Api //////////////////////////////////////////////////////////////

    private fun getMemoNoList() {
        val jsonObject = JSONObject().apply {
            put("memo_form_id", strFormId)
            put("memo_format_lang", radioUtils.isSelectLang)
        }
        RetrofitClient().newInstance()
                .setNetworkCall(this, this, jsonObject, APICode.URL, APICode.GET_MEMO_NO_LIST, isLocation = false, isLoading = true)
    }
    private fun getApproveList() {
        val jsonObject = JSONObject().apply {
            put("memo_form_id", strFormId)
        }
        RetrofitClient().newInstance()
                .setNetworkCall(this, this, jsonObject, APICode.URL, APICode.GET_APPROVE_LIST, isLocation = false, isLoading = true)
    }
    private fun getMemoDetail(memoId: String) {
        val jsonObject = JSONObject().apply {
            put("memo_id", memoId)
        }
        RetrofitClient().newInstance()
                .setNetworkCall(this, this, jsonObject, APICode.URL, APICode.GET_MEMO_DETAIL_NEW, isLocation = false, isLoading = true)
    }
    private fun deleteAttachFile(memoId: String, fileId: String) {
        val jsonObject = JSONObject().apply {
            put("memo_id", memoId)
            put("attach_file_dels", fileId)
            put("attach_file_size", "0")
        }
        RetrofitClient().newInstance()
                .setNetworkCall(this, this, jsonObject, APICode.URL, APICode.UPDATE_ATTACH_FILES, isLocation = false, isLoading = true)
    }

    override fun onSuccessResult(strJson: String, apiCode: String) {
        when (apiCode) {
            APICode.GET_MEMO_NO_LIST -> {
                val model = Gson().fromJson(strJson, CMMemoNoList::class.java)
                when (model.command) {
                    apiCode -> { setMemoNoListDialog(model.data) }
                    else -> showDialogAlert(false, model.message)
                }
            }
            APICode.GET_APPROVE_LIST -> {
                val model = Gson().fromJson(strJson, CMListTo::class.java)
                when (model.command) {
                    apiCode -> {
                        listTo = model.data
                        setViewVisible(blockScroll, true)
                    }
                    else -> showDialogAlert(false, model.message)
                }
            }
            APICode.GET_MEMO_DETAIL_NEW -> {
                val model = Gson().fromJson(strJson, CMMemoDetail::class.java)
                when (model.command) {
                    apiCode -> { setValueFromApi(model) }
                    else -> showDialogAlert(false, model.message)
                }
            }
            APICode.UPDATE_ATTACH_FILES -> {
                val model = Gson().fromJson(strJson, CMModel::class.java)
                when (model.command) {
                    apiCode -> {
                        listPicFile.removeAt(removeIndexTemp)
                        checkFileToList(isPlus = true, isModel = false, isPath = "")
                        showDialogAlert(true, model.message)
                    }
                    else -> showDialogAlert(false, model.message)
                }
            }
        }
    }
    override fun onErrorResult(msg: String, apiCode: String) {}

    ////////////////////////////////////////////////////////////// Function Call Api //////////////////////////////////////////////////////////////

    private fun checkFormBeforeSent() {
        if (checkFormIsNotEmpty(edtGovernment)) {
            if (checkFormIsNotEmpty(strDocNoId, getTextToTrim(tvTitleDocNo))) {
                if (checkFormIsNotEmpty(strDateApi, getTextToTrim(edtDate))) {
                    if (checkFormIsNotEmpty(edtSubject)) {
                        if (checkFormIsNotEmpty(listToApi, R.string.please_select_to)) {
                            if (checkFormIsNotEmpty(edtFromPosition)) {
                                if (checkMemoDetailNotEmpty(edtMeReason, edtMePurpose, edtMeSummary, strCreateByPlatForm)) {
                                    if (checkInputTextAttachment(listPicFile, edtAttachment)) {
                                        if (checkRadioButtonIsNotEmpty(radioUtils.isSelectShowTo, edtShowTo)) {
                                            if (checkRadioButtonIsNotEmpty(radioUtils.isSelectShowFrom, edtFromName)) {

                                                val bundle = Bundle()
                                                getTitleAppBar().let { bundle.putString("menuName", it) }

                                                radioUtils.isSelectConfident.toString().let { bundle.putString("strConfidentID", it) }
                                                radioUtils.isSelectSpeedLevel.toString().let { bundle.putString("strSpeedLevelID", it) }
                                                radioUtils.isSelectShowTo.toString().let { bundle.putString("strShowToID", it) }
                                                radioUtils.isSelectShowFrom.toString().let { bundle.putString("strFromID", it) }

                                                radioUtils.isConfidentName.let { bundle.putString("strConfidentName", it) }
                                                radioUtils.isSpeedLevelName.let { bundle.putString("strSpeedLevelName", it) }

                                                bundle.putString("strDocNoId", strDocNoId) //memo_no_id
                                                getTextToTrim(tvDocNo).let { bundle.putString("strDocNoName", it) } //memo_no_name
                                                getTextToTrim(strFormId).let { bundle.putString("strFormId", it) }
                                                getTextToTrim(edtGovernment).let { bundle.putString("strGovernment", it) }
                                                getTextToTrim(edtSubject).let { bundle.putString("strSubject", it) }
                                                getTextToTrim(edtShowTo).let { bundle.putString("strShowTo", it) }
                                                getTextToTrim(edtAttachment).let { bundle.putString("strAttachment", it) }
                                                getTextToTrim(edtMeReason).let { bundle.putString("strMeReason", it) }
                                                getTextToTrim(edtMePurpose).let { bundle.putString("strMePurpose", it) }
                                                getTextToTrim(edtMeSummary).let { bundle.putString("strMeSummary", it) }
                                                getTextToTrim(edtFromName).let { bundle.putString("strFromName", it) }
                                                getTextToTrim(edtFromPosition).let { bundle.putString("strFromPosition", it) }

                                                bundle.putString("strDateShow", getTextToTrim(edtDate))
                                                bundle.putString("strDateApi", strDateApi)

                                                bundle.putSerializable("listToApi", listToApi)
                                                bundle.putSerializable("listPicFile", listPicFile)

                                                bundle.putString("isFromType", isFromType)
                                                bundle.putString("isFrom", "NewExkasan")
                                                bundle.putString("memo_id", strMemoId)
                                                bundle.putString("strCreateByPlatForm", strCreateByPlatForm.toString())
                                                bundle.putString("strMeDetail", strMeDetail)
                                                bundle.putString("memo_format_lang", radioUtils.isSelectLang)

                                                openActivityWithBundle(InternalFormPvActivity::class.java, bundle)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        when (requestCode) {
            Configs.REQUEST_MENU_CREATE_MEMO -> { }
            else -> { // setActivity CameraConfig
                cameraUtils.setActivityResult(requestCode, resultCode, data)
            }
        }
    }
}