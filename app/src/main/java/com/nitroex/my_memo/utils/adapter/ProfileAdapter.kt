package com.nitroex.my_memo.utils.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nitroex.memo.my_memo.R
import com.nitroex.my_memo.utils.model.CurrentDocument
import kotlinx.android.synthetic.main.list_view_profile.view.*

class ProfileAdapter(private val context: Context, private val list: List<CurrentDocument>) : RecyclerView.Adapter<ProfileViewHolder>() {
    private var listener: OnClickListListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        return ProfileViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val itemView = holder.itemView
        val model = list[position]
        itemView.tvText.text = model.status_name
        itemView.tvNum.text = model.document_total.toString()

        itemView.btnCard.setOnClickListener {
            listener!!.onClickList(model)
        }
    }

    override fun getItemViewType(position: Int): Int { return position }
    override fun getItemCount(): Int { return list.size }

    fun setOnClickListener(listener: OnClickListListener) {
        this.listener = listener
    }
    interface OnClickListListener { fun onClickList(model: CurrentDocument) }
}

class ProfileViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    constructor(parent: ViewGroup) : this(LayoutInflater.from(parent.context).inflate(R.layout.list_view_profile, parent, false))
}