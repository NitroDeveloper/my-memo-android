package com.nitroex.my_memo.utils.attachFile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.nitroex.my_memo.BaseActivity
import com.nitroex.memo.my_memo.R
import com.nitroex.my_memo.service.APICode
import com.nitroex.my_memo.service.RetrofitClient
import com.nitroex.my_memo.utils.CameraUtils
import com.nitroex.my_memo.utils.Configs
import com.nitroex.my_memo.utils.FileUtils
import com.nitroex.my_memo.utils.attachFile.adapter.AttachFileAdapter
import com.nitroex.my_memo.utils.attachFile.model.AttachFile
import com.nitroex.my_memo.utils.listener.CommonResponseListener
import com.nitroex.my_memo.utils.listener.OnCameraCallbackListener
import com.nitroex.my_memo.utils.listener.OnDialogCallbackListener
import com.nitroex.my_memo.utils.listener.OnDialogDismissListener
import com.nitroex.my_memo.utils.model.CMModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_attach_file.*
import kotlinx.android.synthetic.main.activity_attach_file.btnTipsAttachment
import kotlinx.android.synthetic.main.view_title_head.*
import org.json.JSONObject
import java.io.Serializable

class AttachFileActivity : BaseActivity(), CommonResponseListener, AttachFileAdapter.OnAttachFileListener {

    private lateinit var cameraUtils: CameraUtils
    private var listPicFile: ArrayList<AttachFile> = arrayListOf()
    private lateinit var adapterAttFile: AttachFileAdapter
    private val numMaxFile = Configs.AttachMaxFile
    private var strFileDel = ""
    private var strMemoId = "" //รหัสเอกสาร
    private var strAttachment = "" //สิ่งที่แนบมาด้วย
    private var isEditFile = false
    private var isEditAttachment = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attach_file)

        getBundle()
        setClick()
    }
    private fun getBundle() {
        val bundle = getBundleFromIntent(this)
        if (bundle != null) {
            setTitleAppBar(bundle)

            tvSentFrom.text = bundle.getString("strFromName","")
            tvFileNumber.text = FileUtils.isListSize(listPicFile).toString()
            listPicFile = bundle.getSerializable("listPicFile") as ArrayList<AttachFile>
            strMemoId = bundle.getString("memo_id","")
            strAttachment = bundle.getString("strAttachment","")
            edtAttachment.setText(strAttachment)

            setAttachFileList()
        }
    }

    private fun setClick() {
        btnHeadBack.setOnClickListener { onBackPressed() }
        btnSave.setOnClickListener { checkBeforeSentApi() }
        btnTipsAttachment.setOnClickListener { showDialogTipsAttachment() }

        edtAttachment.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                if (getTextToTrim(edtAttachment) != strAttachment) {
                    isEditAttachment=true
                    setEnabledButtonSave()
                }
            }
        })
    }

    private fun checkBeforeSentApi() {
        if (checkInputTextAttachment(listPicFile, edtAttachment)) {
            updateAttachFile()
        }
    }

    //////////////////////////////////// Function Attach File

    private fun setAttachFileList() {
        cameraUtils = CameraUtils().newInstance()
        cameraUtils.setEasyImage(this)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapterAttFile = AttachFileAdapter(this, false)
        adapterAttFile.setOnAttachFileListener(this)
        rvAttachFile.layoutManager = layoutManager
        rvAttachFile.adapter = adapterAttFile

        checkFileToList(true,"") //เพิ่มปุ่มบวกเมื่อยังไม่มีรูปภาพในลิสต์
    }

    private fun checkFileToList(isPlus: Boolean, isPath: String){
        if (isPlus) {
            if(!FileUtils.isButtonAddFile(listPicFile, numMaxFile))//เพิ่มปุ่มบวกเมื่อยังไม่ MaxFile
                listPicFile.add(AttachFile(0, AttachFileAdapter.ADD, "","","","",0,0,""))
        }else{
            listPicFile = FileUtils.setPathToModel(isPath, listPicFile, numMaxFile)
        }
        adapterAttFile.setItem(listPicFile)
        tvFileNumber.text = FileUtils.isListSize(listPicFile).toString()
    }

    override fun onAttachClick() { //Select Add Picture
        cameraUtils.showDialogSelectPic(object : OnCameraCallbackListener {
            override fun onCameraSuccess(isPath: String) {
                setEnabledButtonSave()
                checkFileToList(false, isPath)
            }
            override fun onCameraError(msg: String) { showDialogAlert(false, msg) }
        })
    }

    override fun onRemoveClick(position: Int) {
        showDialogConfirm(object : OnDialogCallbackListener {
            override fun onSubmit() {
                setEnabledButtonSave()

                if (listPicFile[position].id != 0) { //รวมลบไฟล์ส่งเป็นอาเรย์(เฉพาะไฟล์ที่มีอยู่บนระบบ)
                    if (strFileDel.isNotEmpty()) { strFileDel+="," }
                    strFileDel += listPicFile[position].id.toString()
                }
                listPicFile.removeAt(position)
                checkFileToList(true,"")

            }
            override fun onCancel() {}
        }, getString(R.string.want_delete))

    }

    private fun setEnabledButtonSave(){
        isEditFile=true; btnSave.isEnabled = true
        btnSave.setBackgroundResource(R.drawable.shape_gray_dark)
    }

    override fun onImageClick(url: String?, ivImage: ImageView, position: Int) {
        val intent = Intent(this, GalleryActivity::class.java)
        intent.putExtra("gallery_list", listPicFile as Serializable)
        intent.putExtra("index_select", position)
        startActivity(intent)
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

    //////////////////////////////////// Function Attach File


    ////////////////////////////////////////////////////////////// Function Call Api //////////////////////////////////////////////////////////////

    private fun updateAttachFile() {
        val jsonObject = JSONObject().apply {
            var fileSize = 0 //attach file
            for (i in listPicFile.indices){
                if (listPicFile[i].id==0 && listPicFile[i].type_id== AttachFileAdapter.IMAGE) {
                    put("attach_file_$fileSize", listPicFile[i].path)
                    fileSize++
                }
            }
            put("attach_file_size", fileSize.toString())
            put("attach_file_dels", strFileDel)
            put("memo_id", strMemoId)

            //เมื่อแก้ไขรายละเอียดไฟล์แนบหรือลบภาพจนหมดเท่านั้น
            if (isEditAttachment || listPicFile.size==1) {
                put("isEditAttachment", true)
                put("memo_attachment", setTextNToBr(getTextToTrim(edtAttachment)))
            }else{ put("isEditAttachment", false) }
        }
        RetrofitClient().newInstance()
            .setNetworkCall(this, this, jsonObject, APICode.URL, APICode.UPDATE_ATTACH_FILES, isLocation = false, isLoading = true)
    }

    override fun onSuccessResult(strJson: String, apiCode: String) {
        when (apiCode) {
            APICode.UPDATE_ATTACH_FILES -> {
                val model = Gson().fromJson(strJson, CMModel::class.java)
                when (model.command) {
                    apiCode -> {
                        showDialogAlertListener(true, model.message,
                            object : OnDialogDismissListener {
                                override fun onDismiss() { returnRefreshActivityFinish() }
                            })
                    }
                    else -> showDialogAlert(false, model.message)
                }
            }
        }
    }
    override fun onErrorResult(msg: String, apiCode: String) {}

    ////////////////////////////////////////////////////////////// Function Call Api //////////////////////////////////////////////////////////////
}