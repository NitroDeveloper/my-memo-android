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
import java.util.ArrayList

class CopyToAdapter (private val context: Context): RecyclerView.Adapter<CopyViewHolder>() {
  private var listener: OnClickListener? = null
  private var model: MutableList<ListTo> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CopyViewHolder {
        return CopyViewHolder(parent)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CopyViewHolder, position: Int) {
        val itemView =holder.itemView
        val model = model[position]

        itemView.tvNumber.text = (position+1).toString()+"."
        itemView.tvFullName.text = model.emp_name
        itemView.btnRemove.setOnClickListener { listener!!.onClickDeleteCopyTo(model, position) }
    }

    override fun getItemViewType(position: Int): Int { return position }
    override fun getItemCount(): Int { return model.size }

    fun setData(model: List<ListTo>){
        this.model = ArrayList(model)
        notifyDataSetChanged()
    }
    fun  setOnClickListener(listener: OnClickListener){
        this.listener = listener
    }
    interface OnClickListener{ fun onClickDeleteCopyTo(model: ListTo, position: Int)}
}

class CopyViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
      constructor(parent: ViewGroup): this(LayoutInflater.from(parent.context).inflate(R.layout.list_view_to,parent,false))
}
