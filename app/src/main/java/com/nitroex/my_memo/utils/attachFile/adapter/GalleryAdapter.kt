package com.nitroex.my_memo.utils.attachFile.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nitroex.my_memo.BaseActivity
import com.nitroex.memo.my_memo.R
import com.nitroex.my_memo.utils.attachFile.model.AttachFile
import kotlinx.android.synthetic.main.list_gallery_preview.view.*

class GalleryAdapter(val context: Context) : RecyclerView.Adapter<GalleryViewHolder>() {

    private var list: List<AttachFile> = listOf()
    private var listener: OnClickCardListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        return GalleryViewHolder(parent)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val view = holder.itemView

        (context as BaseActivity).setPicGlide(context, list[position].path!!, view.ivPicPreview)
        holder.itemView.ivPicPreview.setOnClickListener {
            listener!!.onClickCard(position)
        }
    }

    fun setItem(list: List<AttachFile>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = list.size

    fun setOnClickCardListener(listener: OnClickCardListener) {
        this.listener = listener
    }

    interface OnClickCardListener {
        fun onClickCard(position: Int)
    }
}

class GalleryViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    constructor(parent: ViewGroup) : this(LayoutInflater.from(parent.context).inflate(R.layout.list_gallery_preview, parent, false))
}
