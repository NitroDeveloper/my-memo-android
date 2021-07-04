package com.nitroex.my_memo.ui.memo_draft.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.nitroex.memo.my_memo.R
import com.nitroex.my_memo.ui.memo_status.model.MemoStatus
import kotlinx.android.synthetic.main.list_memo_draft.view.*
import java.util.*
import kotlin.collections.ArrayList

class DraftMemoAdapter : RecyclerView.Adapter<DraftMemoViewHolder>(), Filterable {
    private var listener: OnClickListListener? = null
    private var listToMain: MutableList<MemoStatus> = mutableListOf()
    private var listToFilter: List<MemoStatus> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DraftMemoViewHolder {
        return DraftMemoViewHolder(parent)
    }

    override fun onBindViewHolder(holder: DraftMemoViewHolder, position: Int) {
        val itemView = holder.itemView
        val model = listToMain[position]
        itemView.tvSubject.text = model.memo_subject
        itemView.tvTypeForm.text = model.memo_form_name
        itemView.tvDate.text = model.memo_create_date
        itemView.tvTime.text = model.memo_create_time
        itemView.btnCard.setOnClickListener { listener!!.onClickList(model) }
        itemView.btnRemove.setOnClickListener { listener!!.onClickRemove(model) }
    }

    override fun getItemViewType(position: Int): Int { return position }

    override fun getItemCount(): Int { return listToMain.size }

    fun setData(model: List<MemoStatus>){
        listToMain = ArrayList(model)
        listToFilter = ArrayList(model)
        notifyDataSetChanged()
    }

    fun setOnClickListener(listener: OnClickListListener) {
        this.listener = listener
    }
    interface OnClickListListener {
        fun onClickList(model: MemoStatus)
        fun onClickRemove(model: MemoStatus)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun convertResultToString(resultValue: Any): String {
                return (resultValue as MemoStatus).memo_subject
            }

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                val list : ArrayList<MemoStatus> = ArrayList()
                if (constraint != null) {
                    for (model in listToFilter) {
                        if (model.memo_subject.toLowerCase(Locale.ROOT).contains(constraint.toString().toLowerCase(Locale.ROOT))) {
                            list.add(model)
                        }
                    }
                    filterResults.values = list
                    filterResults.count = list.size
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                listToMain.clear()
                if (results != null && results.count > 0) {
                    for (obj in results.values as List<*>) {
                        if (obj is MemoStatus) {
                            listToMain.add(obj)
                        }
                    }
                    notifyDataSetChanged()
                } else if (constraint == null) { // no filter, add entire original list back in
                    listToMain.addAll(listToFilter)
                    notifyDataSetChanged()
                }
            }
        }
    }

}

class DraftMemoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    constructor(parent: ViewGroup) : this(LayoutInflater.from(parent.context).inflate(R.layout.list_memo_draft, parent, false))
}