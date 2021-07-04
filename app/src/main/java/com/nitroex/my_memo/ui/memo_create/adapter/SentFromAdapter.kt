package com.nitroex.my_memo.ui.memo_create.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nitroex.memo.my_memo.R
import com.nitroex.my_memo.ui.memo_create.model.Conclusion
import kotlinx.android.synthetic.main.list_view_row.view.*

class SentFromAdapter(private val context: Context) : RecyclerView.Adapter<SentFromViewHolder>() {
    private var listener: OnClickListListener? = null
    private var list: List<Conclusion> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SentFromViewHolder {
        return SentFromViewHolder(parent)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SentFromViewHolder, position: Int) {
        val itemView = holder.itemView
        val model = list[position]
        itemView.tvText.text = model.ccs_name
        itemView.btnCard.setOnClickListener { listener!!.onClickSentFrom(model) }
    }

    override fun getItemViewType(position: Int): Int { return position }
    override fun getItemCount(): Int { return list.size }

    fun setData(model: List<Conclusion>){
        list = ArrayList(model)
        notifyDataSetChanged()
    }

    fun setOnClickListener(listener: OnClickListListener) {
        this.listener = listener
    }
    interface OnClickListListener { fun onClickSentFrom(model: Conclusion) }
}

class SentFromViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    constructor(parent: ViewGroup) : this(LayoutInflater.from(parent.context).inflate(R.layout.list_view_row, parent, false))
}