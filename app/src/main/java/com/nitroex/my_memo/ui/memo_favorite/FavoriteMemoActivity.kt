package com.nitroex.my_memo.ui.memo_favorite

import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.nitroex.my_memo.BaseActivity
import com.nitroex.memo.my_memo.R
import com.nitroex.my_memo.service.APICode
import com.nitroex.my_memo.service.RetrofitClient
import com.nitroex.my_memo.ui.MainActivity
import com.nitroex.my_memo.ui.memo_create.InternalFormActivity
import com.nitroex.my_memo.ui.memo_favorite.adapter.FavoriteMemoAdapter
import com.nitroex.my_memo.ui.memo_status.model.CMMemoStatus
import com.nitroex.my_memo.ui.memo_status.model.MemoStatus
import com.nitroex.my_memo.utils.Configs
import com.nitroex.my_memo.utils.listener.CommonResponseListener
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_favorite_memo.*
import kotlinx.android.synthetic.main.activity_favorite_memo.rvList
import kotlinx.android.synthetic.main.activity_favorite_memo.swipeRefresh
import kotlinx.android.synthetic.main.view_icon_bottom.*
import kotlinx.android.synthetic.main.view_title_head.*
import org.json.JSONObject

class FavoriteMemoActivity : BaseActivity(), CommonResponseListener, FavoriteMemoAdapter.OnClickListListener {
    private var adapter = FavoriteMemoAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_memo)

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
            override fun onQueryTextSubmit(query: String?): Boolean { return false }
            override fun onQueryTextChange(newText: String?): Boolean { adapter.filter.filter(newText); return false }
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
            .setNetworkCall(this, this, JSONObject(), APICode.URL, APICode.GET_FAVORITE_LIST, isLocation = false, isLoading = isLoading)
    }

    override fun onSuccessResult(strJson: String, apiCode: String) {
        swipeRefresh.isRefreshing = false
        val model = Gson().fromJson(strJson, CMMemoStatus::class.java)
        when (model.command) {
            apiCode -> setListAdapter(model.data)
            else -> setTextApiNoData(model.message)
        }
    }
    override fun onErrorResult(msg: String, apiCode: String) {}

    private fun setListAdapter(data: List<MemoStatus>) {
        adapter = FavoriteMemoAdapter()
        adapter.setOnClickListener(this)
        rvList.layoutManager = LinearLayoutManager(this)
        rvList.adapter = adapter
        adapter.setData(data)
    }
    override fun onClickList(model: MemoStatus) {
        if(model.memo_form_id.toString() == Configs.INTERNAL_FORM_ID){
            val bundle = Bundle().apply {
                putString("menuId", model.memo_form_id.toString())
                putString("memo_id", model.memo_id.toString())
                putString("isFromType", Configs.FavoriteForm)
            }
            openActivityWithBundle(InternalFormActivity::class.java, bundle)

        }
    }
}