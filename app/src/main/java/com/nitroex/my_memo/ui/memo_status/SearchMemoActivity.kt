package com.nitroex.my_memo.ui.memo_status

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.nitroex.my_memo.BaseActivity
import com.nitroex.memo.my_memo.R
import com.nitroex.my_memo.service.APICode
import com.nitroex.my_memo.service.RetrofitClient
import com.nitroex.my_memo.ui.MainActivity
import com.nitroex.my_memo.ui.memo_status.adapter.MemoStatusListAdapter
import com.nitroex.my_memo.ui.memo_status.model.CMStatusList
import com.nitroex.my_memo.ui.memo_status.model.StatusList
import com.nitroex.my_memo.utils.DateUtils
import com.nitroex.my_memo.utils.listener.CommonResponseListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_memo_search.*
import kotlinx.android.synthetic.main.dialog_bottom_sheet.view.*
import kotlinx.android.synthetic.main.view_icon_bottom.*
import kotlinx.android.synthetic.main.view_title_head.*
import org.json.JSONObject
import java.util.*

class SearchMemoActivity : BaseActivity(), CommonResponseListener, MemoStatusListAdapter.OnClickListListener {

    private lateinit var dateStart: DateUtils
    private lateinit var dateEnd: DateUtils
    private lateinit var dialogDateS: DatePickerDialog
    private lateinit var dialogDateE: DatePickerDialog

    private lateinit var dialogStatus: BottomSheetDialog
    private lateinit var adapterStatus: MemoStatusListAdapter
    private var modelStatus = arrayListOf<StatusList>()

    private var seDateStart = ""
    private var seDateEnd = ""
    private var seStatusID = ""

    private var isFromProfile = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo_search)

        setView()
        setClick()
    }

    private fun setView() {
        val bundle = getBundleFromIntent(this)
        if (bundle != null) {
            setTitleAppBar(bundle)
            setHideFabOnScroll(scrollView)
            dateStart = DateUtils().newInstance(); dateStart.setEasyDate(this)
            dateEnd = DateUtils().newInstance(); dateEnd.setEasyDate(this)

            getStatusListApi()

            // Set Value From Bundle Or Default
            bundle.apply {
                seDateStart = getString("seDateStart", "")
                seDateEnd = getString("seDateEnd", "")
                edtSearch.setText(getString("seSearch", ""))

                if (getBoolean("isDisableStatus", false)) {
                    isFromProfile=true
                    setViewVisible(ivStatus, false)
                }

                if (seDateStart.isEmpty()) { // set default value
                    dialogDateS = dateStart.getDateDialog(dateStart.getCalendarNow())
                    tvDateStart.text = getString(R.string.all)
                }else{
                    dialogDateS = dateStart.getDateDialog(dateStart.getCalendarApi(seDateStart))
                    tvDateStart.text = dateStart.convertDateApiToShow(seDateStart,false)
                }

                if (seDateEnd.isEmpty()) { // set default value
                    dialogDateE = dateEnd.getDateDialog(dateEnd.getCalendarNow())
                    tvDateEnd.text = getString(R.string.all)
                }else{
                    dialogDateE = dateEnd.getDateDialog(dateEnd.getCalendarApi(seDateEnd))
                    tvDateEnd.text = dateEnd.convertDateApiToShow(seDateEnd,false)
                }

                seStatusID = getString("seStatusID", "")
                if (seStatusID.isEmpty()) { seStatusID=""; tvStatus.text = getString(R.string.all)
                }else{ tvStatus.text = getString("seStatusName", "") }

            }
        }
    }

    private fun setClick() {
        btnHeadBack.setOnClickListener { onBackPressed() }
        btnFabIcon.setOnClickListener { openActivityFinish(MainActivity::class.java) }

        btnBlockDateStart.setOnClickListener {
            dialogDateS.show()
            dialogDateS.setOnDateSetListener { _, year, month, day ->
                val date: Date = dateStart.convertDateByPickerDialog(year, month, day)
                tvDateStart.text = dateStart.getDateFormatShowByBaseLang(this, date)
                seDateStart = dateStart.getDateFormatApi(date)
            }
        }
        btnBlockDateEnd.setOnClickListener {
            dialogDateE.show()
            dialogDateE.setOnDateSetListener { _, year, month, day ->
                val date: Date = dateEnd.convertDateByPickerDialog(year, month, day)
                tvDateEnd.text = dateEnd.getDateFormatShowByBaseLang(this, date)
                seDateEnd = dateEnd.getDateFormatApi(date)
            }
        }
        btnBlockStatus.setOnClickListener {
            if (!isFromProfile) {
                if (modelStatus.isNotEmpty()) { dialogStatus.show() }
            }
        }
        btnSearch.setOnClickListener {
            returnActivityWithBundle()
        }
    }

    private fun returnActivityWithBundle() {
        val bundle = Bundle().apply {
            putString("seDateStart", seDateStart)
            putString("seDateEnd", seDateEnd)
            putString("seStatusID", seStatusID)
            putString("seStatusName", getTextToTrim(tvStatus))
            putString("seSearch", getTextToTrim(edtSearch))
        }
        returnBundleRefreshActivity(bundle); finish()
    }

    private fun setStatusListDialog(data: List<StatusList>) {
        dialogStatus = BottomSheetDialog(this)
        val v = layoutInflater.inflate(R.layout.dialog_bottom_sheet,null)
        dialogStatus.setContentView(v)

        adapterStatus = MemoStatusListAdapter(this)
        adapterStatus.setOnClickListener(this)
        v.rvList.layoutManager = LinearLayoutManager(this)
        v.rvList.adapter = adapterStatus
        v.tvTitle.text = getString(R.string.select_status)
        v.btnClose.setOnClickListener { dialogStatus.dismiss() }

        val list = arrayListOf(StatusList(0, getString(R.string.all)))
        list.addAll(data)

        adapterStatus.setData(list)
    }
    override fun onClickStatusList(model: StatusList) {
        dialogStatus.dismiss()

        seStatusID = if (model.memo_status_id==0) { "" }else{ model.memo_status_id.toString() }
        tvStatus.text = model.memo_status_name
    }

    ////////////////////////////////////////////////////////////// Function Call Api //////////////////////////////////////////////////////////////

    private fun getStatusListApi() {
        val jsonObject = JSONObject().apply {
            put("memo_form_id","")
        }
        RetrofitClient().newInstance()
            .setNetworkCall(this, this, jsonObject, APICode.URL, APICode.GET_MEMO_STATUS_LIST, isLocation = false, isLoading = false)
    }

    override fun onSuccessResult(strJson: String, apiCode: String) {
        when (apiCode) {
            APICode.GET_MEMO_STATUS_LIST -> {
                val model = Gson().fromJson(strJson, CMStatusList::class.java)
                when (model.command) {
                    apiCode -> { modelStatus.addAll(model.data); setStatusListDialog(modelStatus) }
                    else -> showDialogAlert(false, model.message)
                }
            }
        }
    }
    override fun onErrorResult(msg: String, apiCode: String) {}

    ////////////////////////////////////////////////////////////// Function Call Api //////////////////////////////////////////////////////////////
}