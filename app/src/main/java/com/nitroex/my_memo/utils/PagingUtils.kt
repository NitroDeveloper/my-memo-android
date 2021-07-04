package com.nitroex.my_memo.utils

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.nitroex.my_memo.BaseActivity
import com.nitroex.my_memo.ui.memo_favorite.adapter.FavoriteMemoAdapter
import com.nitroex.my_memo.ui.memo_status.adapter.MemoStatusAdapter
import com.nitroex.my_memo.ui.memo_status.model.MemoStatus
import com.nitroex.my_memo.utils.listener.OnPagingCallbackListener

class PagingUtils : BaseActivity() {

    var modelAll: MutableList<MemoStatus> = mutableListOf()
    var modelAPI: MutableList<MemoStatus> = mutableListOf()

    var offset = 0 //Paging offset
    var limit = Configs.PagingLimit //Paging limit
    var isLoadMoreEnd = false //Paging isLoadMoreEnd

    private var instance: PagingUtils? = null
    private lateinit var context: Context
    private lateinit var rvList: RecyclerView
    private lateinit var listener : OnPagingCallbackListener

    private var isType = ""

    private lateinit var adapterMem: MemoStatusAdapter
    private lateinit var adapterFav: FavoriteMemoAdapter

    private var memo = "memo"
    private var favorite = "favorite"

    fun newInstance(): PagingUtils {
        if (instance == null) { instance = PagingUtils() }
        return instance as PagingUtils
    }

    fun setViewPaging(ctx: Context, recyclerView: RecyclerView, adapter: MemoStatusAdapter, listener: OnPagingCallbackListener){
        this.context = ctx; this.rvList = recyclerView; this.adapterMem = adapter; this.listener = listener; this.isType = memo
        setPagingRecyclerView()
    }
    fun setViewPaging(ctx: Context, recyclerView: RecyclerView, adapter: FavoriteMemoAdapter, listener: OnPagingCallbackListener){
        this.context = ctx; this.rvList = recyclerView; this.adapterFav = adapter; this.listener = listener; this.isType = favorite
        setPagingRecyclerView()
    }

    private fun setPagingRecyclerView(){
        rvList.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (!recyclerView.canScrollVertically(1)) {
                    if (!isLoadMoreEnd) {

                        val layoutManager = recyclerView.layoutManager
                        val lastPosition = layoutManager!!.itemCount

                        //กันการถูกเรียกซ้ำตำแหน่งเดิม
                        if (lastPosition != offset) {
                            offset = lastPosition
                            if (offset >= limit) {
//                                setViewVisible(pgLoadMore, true)
                                listener.onLoadMore()
                            }
                        }
                    }
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    fun setAdapterLoadMore(data: MutableList<MemoStatus>) {
//        setViewVisible(pgLoadMore, false)
        modelAPI = data
        if (modelAPI.isNotEmpty()) {
            modelAll.addAll(modelAPI)


            when (isType) {
                memo -> {
                    adapterMem.setData(modelAll)
                }
                favorite -> {
                    adapterFav.setData(modelAll)
                }
            }
        }
    }

    // Clear Default Offset=0
    fun setClearDefaultPaging(){
        try { modelAll.clear()
            offset = 0
            isLoadMoreEnd = false
        } catch (e: Exception) { }
    }

    fun checkFirstGetList(): Boolean { return offset==0 }

}