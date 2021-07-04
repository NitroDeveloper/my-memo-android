package com.nitroex.my_memo.ui.memo_status.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nitroex.memo.my_memo.R
import com.nitroex.my_memo.ui.memo_status.model.StatusList
import kotlinx.android.synthetic.main.list_view_row.view.*

class MemoStatusListAdapter(private val context: Context) : RecyclerView.Adapter<MemoStatusListViewHolder>() {
    private var listener: OnClickListListener? = null
    private var list: List<StatusList> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoStatusListViewHolder {
        return MemoStatusListViewHolder(parent)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MemoStatusListViewHolder, position: Int) {
        val itemView = holder.itemView
        val model = list[position]
        itemView.tvText.text = model.memo_status_name
        itemView.btnCard.setOnClickListener { listener!!.onClickStatusList(model) }
    }

    override fun getItemViewType(position: Int): Int { return position }
    override fun getItemCount(): Int { return list.size }

    fun setData(model: List<StatusList>){
        list = ArrayList(model)
        notifyDataSetChanged()
    }

    fun setOnClickListener(listener: OnClickListListener) {
        this.listener = listener
    }
    interface OnClickListListener { fun onClickStatusList(model: StatusList) }
}

class MemoStatusListViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    constructor(parent: ViewGroup) : this(LayoutInflater.from(parent.context).inflate(R.layout.list_view_row, parent, false))
}