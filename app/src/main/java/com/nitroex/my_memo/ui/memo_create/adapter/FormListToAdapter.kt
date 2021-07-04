package com.nitroex.my_memo.ui.memo_create.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nitroex.memo.my_memo.R
import com.nitroex.my_memo.ui.memo_create.model.ListTo
import kotlinx.android.synthetic.main.list_view_to.view.*

class FormListToAdapter(private val context: Context) : RecyclerView.Adapter<ViewHolder>() {
    private var listener: OnClickListListener? = null
    private var listTo: MutableList<ListTo> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemView = holder.itemView
        val model = listTo[position]

        itemView.tvNumber.text = (position+1).toString()+"."
        itemView.tvFullName.text = model.emp_name
        itemView.btnRemove.setOnClickListener { listener!!.onDeleteListTo(model, position) }
    }

    override fun getItemViewType(position: Int): Int { return position }
    override fun getItemCount(): Int { return listTo.size }

    fun setData(model: List<ListTo>){
        listTo = ArrayList(model)
        notifyDataSetChanged()
    }

    fun setOnClickListener(listener: OnClickListListener) {
        this.listener = listener
    }
    interface OnClickListListener { fun onDeleteListTo(model: ListTo, position: Int) }
}

class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    constructor(parent: ViewGroup) : this(LayoutInflater.from(parent.context).inflate(R.layout.list_view_to, parent, false))
}