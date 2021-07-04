package com.nitroex.my_memo.ui.memo_draft

import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.nitroex.my_memo.BaseActivity
import com.nitroex.memo.my_memo.R
import com.nitroex.my_memo.service.APICode
import com.nitroex.my_memo.service.RetrofitClient
import com.nitroex.my_memo.ui.MainActivity
import com.nitroex.my_memo.ui.memo_create.InternalFormActivity
import com.nitroex.my_memo.ui.memo_draft.adapter.DraftMemoAdapter
import com.nitroex.my_memo.ui.memo_status.model.CMMemoStatus
import com.nitroex.my_memo.ui.memo_status.model.MemoStatus
import com.nitroex.my_memo.utils.Configs
import com.nitroex.my_memo.utils.listener.CommonResponseListener
import com.nitroex.my_memo.utils.listener.OnDialogCallbackListener
import com.nitroex.my_memo.utils.model.CMModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_draft_memo.*
import kotlinx.android.synthetic.main.activity_draft_memo.rvList
import kotlinx.android.synthetic.main.activity_draft_memo.swipeRefresh
import kotlinx.android.synthetic.main.view_icon_bottom.*
import kotlinx.android.synthetic.main.view_title_head.*
import org.json.JSONObject

class DraftMemoActivity : BaseActivity(), CommonResponseListener, DraftMemoAdapter.OnClickListListener {

    private var adapter = DraftMemoAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draft_memo)

        setClick()
        setView()
    }

    private fun setClick() {
        btnHeadBack.setOnClickListener { onBackPressed() }
        btnFabIcon.setOnClickListener { openActivityFinish(MainActivity::class.java) }

        swipeRefresh.setOnRefreshListener {
            getListDataApi(false)
            setClearSearchView(searchView)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText); return false
            }
        })
    }

    override fun onBackPressed() {
        openActivityFinish(MainActivity::class.java)
    }

    private fun setView() {
        val bundle = getBundleFromIntent(this)
        if (bundle != null) {
            setTitleAppBar(bundle)
            setHideFabOnScroll(rvList)
            getListDataApi(true)
        }
    }

    private fun getListDataApi(isLoading: Boolean) {
        RetrofitClient().newInstance()
            .setNetworkCall(this, this, JSONObject(), APICode.URL, APICode.GET_DRAFT_LIST, isLocation = false, isLoading = isLoading)
    }
    private fun getMemoDelete(model: MemoStatus) {
        setViewVisible(rvList,false)
        val jsonObject = JSONObject().apply { put("memo_id", model.memo_id) }
        RetrofitClient().newInstance()
            .setNetworkCall(this, this, jsonObject, APICode.URL, APICode.DELETE_DRAFT_MEMO, isLocation = false, true)
    }

    override fun onSuccessResult(strJson: String, apiCode: String) {
        swipeRefresh.isRefreshing = false

        when (apiCode) {
            APICode.GET_DRAFT_LIST -> {
                val model = Gson().fromJson(strJson, CMMemoStatus::class.java)
                when (model.command) {
                    apiCode -> { setListAdapter(model.data); setViewVisible(rvList,true) }
                    else -> setTextApiNoData(model.message)
                }
            }
            APICode.DELETE_DRAFT_MEMO -> {
                val model = Gson().fromJson(strJson, CMModel::class.java)
                when (model.command) {
                    apiCode -> {
                        showDialogAlert(true, model.message)

                        swipeRefresh.isRefreshing = true
                        getListDataApi(false)
                        setClearSearchView(searchView)
                    }
                    else -> showDialogAlert(false, model.message)
                }
            }
        }

    }

    override fun onErrorResult(msg: String, apiCode: String) {}

    private fun setListAdapter(data: List<MemoStatus>) {
        adapter = DraftMemoAdapter()
        adapter.setOnClickListener(this)
        rvList.layoutManager = LinearLayoutManager(this)
        rvList.adapter = adapter
        adapter.setData(data)
    }

    override fun onClickRemove(model: MemoStatus) {
        showDialogConfirm(object : OnDialogCallbackListener {
            override fun onSubmit() { getMemoDelete(model) }
            override fun onCancel() {}
        },getString(R.string.tx_delete))
    }

    override fun onClickList(model: MemoStatus) {
        if (model.memo_form_id.toString() == Configs.INTERNAL_FORM_ID){
            val bundle = Bundle().apply {
                putString("menuId", model.memo_form_id.toString())
                putString("memo_id", model.memo_id.toString())
                putString("isFromType", Configs.DraftForm)
            }
            openActivityWithBundle(InternalFormActivity::class.java, bundle)
        }
    }

}