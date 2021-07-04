package com.nitroex.my_memo.ui.memo_status

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.nitroex.my_memo.BaseActivity
import com.nitroex.memo.my_memo.R
import com.nitroex.my_memo.service.APICode
import com.nitroex.my_memo.service.RetrofitClient
import com.nitroex.my_memo.ui.MainActivity
import com.nitroex.my_memo.ui.memo_create.InternalFormPvActivity
import com.nitroex.my_memo.ui.memo_status.adapter.MemoStatusAdapter
import com.nitroex.my_memo.ui.memo_status.model.CMMemoStatus
import com.nitroex.my_memo.ui.memo_status.model.MemoStatus
import com.nitroex.my_memo.utils.Configs
import com.nitroex.my_memo.utils.PagingUtils
import com.nitroex.my_memo.utils.listener.CommonResponseListener
import com.nitroex.my_memo.utils.listener.OnDialogCallbackListener
import com.nitroex.my_memo.utils.listener.OnPagingCallbackListener
import com.nitroex.my_memo.utils.model.CMModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_memo_status.*
import kotlinx.android.synthetic.main.view_icon_bottom.*
import kotlinx.android.synthetic.main.view_title_head.*
import org.json.JSONObject

class MemoStatusActivity : BaseActivity(), CommonResponseListener, MemoStatusAdapter.OnClickListListener {

    private lateinit var paging: PagingUtils
    private var adapter = MemoStatusAdapter()

    private var isFromTodoList = false
    private var isFromStatus = false
    private var isFromProfile = false
    private var isFirst = true
    private var txtTitleSearch = ""
    private var isMyAction = 0

    private var seSearch = ""
    private var seDateStart = ""
    private var seDateEnd = ""
    private var seStatusID = ""
    private var seStatusName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo_status)

        setClick()
        setView()
    }

    private fun setClick() {
        btnHeadBack.setOnClickListener { onBackPressed() }
        btnFabIcon.setOnClickListener { onBackPressed() }

        swipeRefresh.setOnRefreshListener {
            clearValueSearch()
            getListDataApi(false)
        }
        btnTitleAction.setOnClickListener {
            val bundle = Bundle().apply {
                putString("menuName", txtTitleSearch)
                putString("seDateStart", seDateStart)
                putString("seDateEnd", seDateEnd)
                putString("seStatusID", seStatusID)
                putString("seStatusName", seStatusName)
                putString("seSearch", seSearch)
                putBoolean("isDisableStatus", isFromProfile)
            }
            openActivityWithBundleForResult(SearchMemoActivity::class.java, bundle, Configs.REQUEST_SEARCH_MEMO)
        }
    }

    override fun onBackPressed() {
        if (isFromProfile) {
            super.onBackPressed()
        }else{
            openActivityFinish(MainActivity::class.java)
        }
    }

    private fun setView() {
        val bundle = getBundleFromIntent(this)
        if (bundle != null) {
            setTitleAppBar(bundle)
            setHideFabOnScroll(rvList)
            btnTitleAction.setImageResource(R.drawable.advance_search)
            setViewVisible(btnTitleAction,true)
            paging = PagingUtils().newInstance()

            // set value from menu activity
            when (bundle.getInt("menuAction",0)) {
                Configs.REQUEST_MENU_STATUS_MEMO -> {
                    isFromStatus=true; isMyAction=0
                    txtTitleSearch = getString(R.string.memo_status_search)
                }
                Configs.REQUEST_MENU_TODO_LIST_MEMO -> {
                    isFromTodoList=true; isMyAction=1
                    txtTitleSearch = getString(R.string.search_action_list)
                }
                Configs.REQUEST_MENU_PROFILE -> {
                    isFromProfile=true; isMyAction=1
                    txtTitleSearch = getString(R.string.search_action_list)
                    seStatusID = bundle.getString("isStatusId","")
                    seStatusName = bundle.getString("isStatusName","")
                    if (seStatusID=="0") {seStatusID=""}
                }
            }
            getListDataApi(true)
        }
    }

    private fun getListDataApi(isLoading: Boolean) {
        val jsonObject = JSONObject().apply {
            put("division_id", "")
            put("department_id", "")
            put("section_id", "")
            put("memo_key", seSearch)
            put("memo_type_id", "")
            put("start_date", seDateStart)
            put("end_date", seDateEnd)
            put("memo_status_id", seStatusID)
            put("action_emp_com_id", "")
            put("my_action", isMyAction)
            put("memo_form_id","")
            put("offset", paging.offset)
            put("limit", paging.limit)
        }
        isLog("GET_MEMO_LIST: offset", paging.offset.toString()) //mock
        isLog("GET_MEMO_LIST: limit", paging.limit.toString()) //mock

        RetrofitClient().newInstance()
            .setNetworkCall(this, this, jsonObject, APICode.URL, APICode.GET_MEMO_LIST, isLocation = false, isLoading = isLoading)
    }

    private fun setFavoriteMemo(model: MemoStatus) {
        val jsonObject = JSONObject().apply {
            put("memo_id", model.memo_id)
        }
        RetrofitClient().newInstance()
            .setNetworkCall(this, this, jsonObject, APICode.URL, APICode.FAVORITE_MEMO, isLocation = false, isLoading = false)
    }
    private fun setResendMemo(model: MemoStatus) {
        val jsonObject = JSONObject().apply {
            put("memo_id", model.memo_id)
        }
        RetrofitClient().newInstance()
            .setNetworkCall(this, this, jsonObject, APICode.URL, APICode.RESENT_MEMO, isLocation = false, isLoading = false)
    }

    override fun onSuccessResult(strJson: String, apiCode: String) {
        swipeRefresh.isRefreshing = false
        when (apiCode) {
            APICode.GET_MEMO_LIST -> {
                setViewVisible(rvList,true)

                val model=Gson().fromJson(strJson, CMMemoStatus::class.java)
                when (model.command) {
                    apiCode -> {
                        if (model.data.size!=paging.limit) paging.isLoadMoreEnd=true
                        if (paging.checkFirstGetList()) {
                            setListAdapter(model.data)
                        }else{
                            try {
                                paging.setAdapterLoadMore(model.data as MutableList)
                            } catch (e: Exception) {
                                isLog("error", e.message.toString())
                            }
                        }
                    }
                    else -> {
                        paging.isLoadMoreEnd=true
                        if (paging.checkFirstGetList()) {
                            clearValueSearch()
                            if (isFirst) { setTextApiNoData(model.message) //เมื่อเข้าครั้งแรก
                            }else{ showDialogAlert(this, false, model.message) } //มาจากค้นหา
                        }
                    }
                }
            }
            APICode.FAVORITE_MEMO -> {
                val model = Gson().fromJson(strJson, CMModel::class.java)
                if (model.command != apiCode) { showDialogAlert(false, model.message) }
            }
            APICode.RESENT_MEMO -> {
                val model = Gson().fromJson(strJson, CMModel::class.java)
                when (model.command) {
                    apiCode -> { showDialogAlert(true, model.message) }
                    else -> { showDialogAlert(false, model.message) }
                }
            }
        }
    }
    override fun onErrorResult(msg: String, apiCode: String) {}

    private fun setListAdapter(data: List<MemoStatus>) {
        adapter = MemoStatusAdapter()
        adapter.setOnClickListener(this)
        rvList.layoutManager = LinearLayoutManager(this)
        rvList.adapter = adapter
        adapter.setData(data)

        //PagingUtils
        paging = PagingUtils().newInstance()
        paging.setViewPaging(this, rvList, adapter, object : OnPagingCallbackListener {
            override fun onLoadMore() {
                getListDataApi(true)
            }
            override fun onError(msg: String) {
                showDialogAlert(false, msg)
            }
        })
        paging.modelAll = data.toMutableList()
    }
    override fun onClickList(model: MemoStatus) {
        if(model.memo_form_id.toString() == Configs.INTERNAL_FORM_ID){
            val bundle = Bundle().apply {
                putString("menuName", getString(R.string.memodetail))
                putString("isFrom", "MemoStatus")
                putString("memo_id", model.memo_id.toString())
            }
            openActivityWithBundleForResult(InternalFormPvActivity::class.java, bundle, Configs.REQUEST_PREVIEW_MEMO)

        }
    }
    override fun onClickReSent(model: MemoStatus) {
        showDialogConfirm(object : OnDialogCallbackListener {
            override fun onSubmit() { setResendMemo(model) }
            override fun onCancel() {}
        }, getString(R.string.txt_resend))
    }
    override fun onClickFavorite(model: MemoStatus) { setFavoriteMemo(model) }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) return
        when (requestCode) {
            Configs.REQUEST_SEARCH_MEMO -> {
                seDateStart = getStringFromBundle("seDateStart", data!!)
                seDateEnd = getStringFromBundle("seDateEnd", data)
                seStatusID = getStringFromBundle("seStatusID", data)
                seStatusName = getStringFromBundle("seStatusName", data)
                seSearch = getStringFromBundle("seSearch", data)

                refreshListData()
            }
            Configs.REQUEST_PREVIEW_MEMO -> { refreshListData() }
        }
    }

    ////////////////////////////////// Function In Activity //
    private fun clearValueSearch() {
        paging = PagingUtils().newInstance()
        if (isFromProfile) {
            seSearch = ""; seDateStart = ""; seDateEnd = ""
        }else{
            seSearch = ""; seDateStart = ""; seDateEnd = ""; seStatusID = ""; seStatusName = ""
        }
    }

    private fun refreshListData(){
        paging = PagingUtils().newInstance()
        isFirst = false
        setViewVisible(rvList,false)
        getListDataApi(true)
    }
    ////////////////////////////////// Function In Activity //

}