package com.nitroex.my_memo.ui.memo_status

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.nitroex.my_memo.BaseActivity
import com.nitroex.memo.my_memo.R
import com.nitroex.my_memo.service.APICode
import com.nitroex.my_memo.service.RetrofitClient
import com.nitroex.my_memo.ui.MainActivity
import com.nitroex.my_memo.ui.memo_status.adapter.MemoHistoryAdapter
import com.nitroex.my_memo.ui.memo_status.model.CMMemoHistory
import com.nitroex.my_memo.ui.memo_status.model.MemoHistory
import com.nitroex.my_memo.utils.listener.CommonResponseListener
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_memo_history.*
import kotlinx.android.synthetic.main.activity_memo_history.rvList
import kotlinx.android.synthetic.main.view_icon_bottom.*
import kotlinx.android.synthetic.main.view_title_head.*
import org.json.JSONObject

class MemoHistoryActivity : BaseActivity(), CommonResponseListener {
    private var strMemoId = "" //รหัสเอกสาร
    private var adapter = MemoHistoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo_history)

        getBundle()
        setClick()
    }

    private fun getBundle() {
        val bundle = getBundleFromIntent(this)
        if (bundle != null) {
            setTitleAppBar(getString(R.string.history))
            setHideFabOnScroll(blockScroll)
            strMemoId = bundle.getString("memo_id", "")
            setViewInVisible(btnTitleAction,false)

            getMemoHistory()
        }
    }

    private fun setClick() {
        btnHeadBack.setOnClickListener { onBackPressed() }
        btnFabIcon.setOnClickListener { openActivityFinish(MainActivity::class.java) }
    }

    private fun getMemoHistory() {
        val jsonObject = JSONObject().apply {
            put("memo_id", strMemoId)
        }
        RetrofitClient().newInstance()
            .setNetworkCall(this, this, jsonObject, APICode.URL, APICode.GET_MEMO_HISTORY, isLocation = false, isLoading = true)
    }

    override fun onSuccessResult(strJson: String, apiCode: String) {
        when (apiCode) {
            APICode.GET_MEMO_HISTORY -> {
                setViewVisible(rvList,true)
                val model = Gson().fromJson(strJson, CMMemoHistory::class.java)
                when (model.command) {
                    apiCode -> {
                        setListAdapter(model.data)
                    }
                    else -> { showDialogAlert(false,  getString(R.string.data_not_found)) }
                }
            }
        }
    }
    override fun onErrorResult(msg: String, apiCode: String) {}

    private fun setListAdapter(data: List<MemoHistory>) {
        adapter = MemoHistoryAdapter()
//        adapter.setOnClickListener(this)
        rvList.layoutManager = LinearLayoutManager(this)
        rvList.adapter = adapter
        adapter.setData(data)
    }
//    override fun onClickList(model: MemoHistory) {}

}