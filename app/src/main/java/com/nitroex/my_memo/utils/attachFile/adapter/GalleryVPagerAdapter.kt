package com.nitroex.my_memo.utils.attachFile.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nitroex.my_memo.BaseActivity
import com.nitroex.memo.my_memo.R
import com.nitroex.my_memo.utils.attachFile.model.AttachFile
import kotlinx.android.synthetic.main.list_gallery_show_zoom.view.*

class GalleryVPagerAdapter(val context: Context) : RecyclerView.Adapter<GalleryVPagerViewHolder>() {
    private var list: List<AttachFile> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryVPagerViewHolder {
        return GalleryVPagerViewHolder(parent)
    }

    override fun onBindViewHolder(holder: GalleryVPagerViewHolder, position: Int) {
        val view = holder.itemView

        (context as BaseActivity).setPicGlide(context, list[position].path, view.ivPicShow)
         view.tvImg.text = list[position].file_name
         view.tvName.text = list[position].attach_info
    }

    fun setItem(list: List<AttachFile>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = list.size
}

class GalleryVPagerViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    constructor(parent: ViewGroup) : this(LayoutInflater.from(parent.context).inflate(R.layout.list_gallery_show_zoom, parent, false))

}
