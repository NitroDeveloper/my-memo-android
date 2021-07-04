package com.nitroex.my_memo.ui.memo_status.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nitroex.my_memo.BaseActivity
import com.nitroex.memo.my_memo.R
import com.nitroex.my_memo.ui.memo_status.model.MemoHistory
import com.nitroex.my_memo.ui.memo_status.model.MemoStatus
import com.nitroex.my_memo.utils.Configs
import kotlinx.android.synthetic.main.list_memo_history.view.*
import kotlin.collections.ArrayList

class MemoHistoryAdapter : RecyclerView.Adapter<MemoHistoryViewHolder>() {
    private var listener: OnClickListListener? = null
    private var list: MutableList<MemoHistory> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoHistoryViewHolder {
        return MemoHistoryViewHolder(parent)
    }

    override fun onBindViewHolder(holder: MemoHistoryViewHolder, position: Int) {

        val itemView = holder.itemView
        val model = list[position]

        itemView.apply {
            val ctx = (context as BaseActivity)

            val txtName = if (model.emp_pos_initial.isNotEmpty())
                "${model.emp_name} (${model.emp_pos_initial})"
            else model.emp_name

            tvName.text = txtName
            tvDate.text = model.issue_date
            tvTime.text = model.issue_time
            tvStatus.text = model.memo_status_name
            tvComment.text = ctx.setTextBrToN(ctx.setTextEmptyAddDef(model.memo_comment, context.getString(R.string.do_not_comment)))
            setViewStatusColor(itemView, model.memo_status_id)
        }
    }

    override fun getItemViewType(position: Int): Int { return position }
    override fun getItemCount(): Int { return list.size }

    fun setData(model: List<MemoHistory>){
        list = ArrayList(model)
        notifyDataSetChanged()
    }

    fun setOnClickListener(listener: OnClickListListener) {
        this.listener = listener
    }
    interface OnClickListListener {
        fun onClickList(model: MemoStatus)
    }

    private fun setViewStatusColor(itemView: View, memoStatusId: String) {
        itemView.apply {
            val colorInt:Int
            val drawableInt:Int

            when (memoStatusId.toInt()) {
                Configs.WAIT_APPROVE -> {
                    colorInt = R.color.orange
                    drawableInt = R.drawable.status_warning
                }
                Configs.APPROVE -> {
                    colorInt = R.color.green
                    drawableInt = R.drawable.status_success
                }
                Configs.DISAPPROVE -> {
                    colorInt = R.color.red
                    drawableInt = R.drawable.status_danger
                }
                Configs.CANCELED -> {
                    colorInt = R.color.red
                    drawableInt = R.drawable.status_danger
                }else -> {
                colorInt = R.color.orange
                drawableInt = R.drawable.status_warning
                }
            }
            ivStatus.setBackgroundResource(drawableInt)
            tvStatus.setTextColor(ContextCompat.getColor(context, colorInt))
        }
    }
}

class MemoHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    constructor(parent: ViewGroup) : this(LayoutInflater.from(parent.context).inflate(R.layout.list_memo_history, parent, false))
}