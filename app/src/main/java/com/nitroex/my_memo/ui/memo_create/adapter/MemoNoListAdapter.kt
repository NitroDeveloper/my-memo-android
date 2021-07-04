package com.nitroex.my_memo.ui.memo_create.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nitroex.memo.my_memo.R
import com.nitroex.my_memo.ui.memo_create.model.MemoNoList
import kotlinx.android.synthetic.main.list_view_row.view.*

class MemoNoListAdapter(private val context: Context) : RecyclerView.Adapter<MemoNoListViewHolder>() {
    private var listener: OnClickListListener? = null
    private var model: MutableList<MemoNoList> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoNoListViewHolder {
        return MemoNoListViewHolder(parent)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MemoNoListViewHolder, position: Int) {
        val itemView = holder.itemView
        val model = model[position]

        itemView.tvText.text = model.mno_key_name
        itemView.btnCard.setOnClickListener { listener!!.onClickMemoNoList(model, position) }
    }

    override fun getItemViewType(position: Int): Int { return position }
    override fun getItemCount(): Int { return model.size }

    fun setData(model: List<MemoNoList>){
        this.model = ArrayList(model)
        notifyDataSetChanged()
    }

    fun setOnClickListener(listener: OnClickListListener) {
        this.listener = listener
    }
    interface OnClickListListener { fun onClickMemoNoList(model: MemoNoList, position: Int) }
}

class MemoNoListViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    constructor(parent: ViewGroup) : this(LayoutInflater.from(parent.context).inflate(R.layout.list_view_row, parent, false))
}