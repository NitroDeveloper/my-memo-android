package com.nitroex.my_memo.utils.attachFile.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.nitroex.memo.my_memo.R
import com.nitroex.my_memo.utils.attachFile.model.AttachFile
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class AttachFileAdapter(private val context: Context, private val showOnly: Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var listener: OnAttachFileListener? = null
    private var list: List<AttachFile> = listOf()

    override fun getItemCount(): Int { return list.size }
    override fun getItemViewType(position: Int): Int { return list[position].type_id }

    fun setItem(list: List<AttachFile>){
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ADD -> { val view = LayoutInflater.from(parent.context).inflate(R.layout.view_add_attach_plus, parent, false)
                AddFileViewHolder(view)
            }
            IMAGE -> { val view = LayoutInflater.from(parent.context).inflate(R.layout.view_add_attach_image, parent, false)
                ImageFileViewHolder(view)
            }
            else -> { val view = LayoutInflater.from(parent.context).inflate(R.layout.view_add_attach_image, parent, false)
                ImageFileViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AddFileViewHolder) {
            setUpAddView(holder)
        } else if (holder is ImageFileViewHolder) {
            setUpImageView(holder, position)
        }
    }

    private fun setUpAddView(holder: AddFileViewHolder) {
        holder.ivAdd.setOnClickListener { listener!!.onAttachClick() }
    }

    private fun setUpImageView(holder: ImageFileViewHolder, position: Int) {
        holder.ivImage.transitionName = list[position].path
        val reqOptions = RequestOptions.placeholderOf(R.drawable.placeholder_gallary)
        Glide.with(context).asBitmap().load(list[position].path).apply(reqOptions).into(holder.ivImage)

        holder.ivImage.setOnClickListener { listener!!.onImageClick(list[position].path, holder.ivImage, position) }

        if (showOnly) { holder.btnRemove.visibility = View.GONE
        }else { holder.btnRemove.setOnClickListener { listener!!.onRemoveClick(position) } }
    }

    fun setOnAttachFileListener(listener: OnAttachFileListener) {
        this.listener = listener
    }

    interface OnAttachFileListener {
        fun onAttachClick()
        fun onImageClick(url: String?, ivImage: ImageView, position: Int)
        fun onRemoveClick(position: Int)
    }

    companion object {
        const val ADD = 1
        const val IMAGE = 2
    }

    class AddFileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivAdd: ImageView = itemView.findViewById(R.id.add_file)
    }

    class ImageFileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivImage: ImageView = itemView.findViewById(R.id.image_attach)
        var btnRemove: ImageButton = itemView.findViewById(R.id.btn_remove)

    }
}
