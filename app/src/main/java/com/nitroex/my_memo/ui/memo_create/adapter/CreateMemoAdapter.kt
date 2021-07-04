package com.nitroex.my_memo.ui.memo_create.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nitroex.memo.my_memo.R
import com.nitroex.my_memo.ui.memo_create.model.GetFormList
import kotlinx.android.synthetic.main.list_memo_create.view.*

class CreateMemoAdapter(private val context: Context, private val list: List<GetFormList>) : RecyclerView.Adapter<CreateMemoViewHolder>() {
    private var listener: OnClickListListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreateMemoViewHolder {
        return CreateMemoViewHolder(parent)
    }

    override fun onBindViewHolder(holder: CreateMemoViewHolder, position: Int) {
        val itemView = holder.itemView
        val model = list[position]
        itemView.tvTitle.text = model.mf_form_name
        itemView.tvSubTitle.text = model.mf_form_detail
        itemView.btnCard.setOnClickListener { listener!!.onClickList(model) }
    }

    override fun getItemViewType(position: Int): Int { return position }
    override fun getItemCount(): Int { return list.size }

    fun setOnClickListener(listener: OnClickListListener) {
        this.listener = listener
    }
    interface OnClickListListener { fun onClickList(model: GetFormList) }
}

class CreateMemoViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    constructor(parent: ViewGroup) : this(LayoutInflater.from(parent.context).inflate(R.layout.list_memo_create, parent, false))
}