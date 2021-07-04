package com.nitroex.my_memo.ui.memo_status.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nitroex.memo.my_memo.R
import com.nitroex.my_memo.ui.memo_status.model.MemoStatus
import com.nitroex.my_memo.utils.Configs
import kotlinx.android.synthetic.main.list_memo_status.view.*
import kotlin.collections.ArrayList

class MemoStatusAdapter : RecyclerView.Adapter<MemoStatusViewHolder>() {
    private var listener: OnClickListListener? = null
    private var list: MutableList<MemoStatus> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoStatusViewHolder {
        return MemoStatusViewHolder(parent)
    }

    override fun onBindViewHolder(holder: MemoStatusViewHolder, position: Int) {

        val itemView = holder.itemView
        val model = list[position]

        itemView.apply {
            tvSubject.text = model.memo_subject
            tvMemoNo.text = model.memo_no
            tvTypeForm.text = model.memo_form_name
            tvDate.text = model.memo_create_date
            tvTime.text = model.memo_create_time

            tvToName.text = model.memo_action_name
            tvFromName.text = model.memo_from_name
            tvStatus.text = model.memo_status_name
            tvDuTime.text = model.memo_last_update

            if (model.show_view_icon==1) { btnEye.visibility = View.VISIBLE }
            if (model.show_resent_button==1) { blockReSend.visibility = View.VISIBLE }

            setViewImageFavorite(false, itemView, position, model.memo_favorite_status)
            setViewStatusColor(itemView, model.memo_status_id)

            btnCard.setOnClickListener { listener!!.onClickList(model) }
            btnResend.setOnClickListener { listener!!.onClickReSent(model)}
            btnFavorite.setOnClickListener {
                listener!!.onClickFavorite(model)
                setViewImageFavorite( true, itemView, position, model.memo_favorite_status)
            }
        }
    }

    override fun getItemViewType(position: Int): Int { return position }

    override fun getItemCount(): Int { return list.size }

    fun setData(model: List<MemoStatus>){
        list = ArrayList(model)
        notifyDataSetChanged()
    }

    fun setOnClickListener(listener: OnClickListListener) {
        this.listener = listener
    }
    interface OnClickListListener {
        fun onClickList(model: MemoStatus)
        fun onClickReSent(model: MemoStatus)
        fun onClickFavorite(model: MemoStatus)
    }

    private fun setViewImageFavorite(isClick: Boolean, itemView: View, position: Int, statusID: Int) {
        if(isClick) { //มาจากการกดเพื่อเปลี่ยนสถานะ
            when (statusID) {
                0 -> { list[position].memo_favorite_status = 1 }
                1 -> { list[position].memo_favorite_status = 0 }
            }
            notifyDataSetChanged()

        }else{ //มาจากข้อมูลโมเดลดั้งเดิม
            when (statusID) {
                0 -> { itemView.btnFavorite.setBackgroundResource(R.drawable.favoritememobutton) }
                1 -> { itemView.btnFavorite.setBackgroundResource(R.drawable.favoritememobuttonselected) }
            }
        }
    }

    private fun setViewStatusColor(itemView: View, memoStatusId: Int) {
        itemView.apply {
            val colorInt:Int
            val drawableInt:Int

            when (memoStatusId) {
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

            tvStatus.setTextColor(ContextCompat.getColor(context, colorInt))
            ivStatus.setBackgroundResource(drawableInt)
        }
    }
}

class MemoStatusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    constructor(parent: ViewGroup) : this(LayoutInflater.from(parent.context).inflate(R.layout.list_memo_status, parent, false))
}