package com.nitroex.my_memo.utils.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.nitroex.memo.my_memo.R
import com.nitroex.my_memo.ui.memo_create.model.ListTo
import kotlinx.android.synthetic.main.list_view_row.view.*
import java.util.*

class AddCopyToListAdapter : RecyclerView.Adapter<ViewHolderCopy>(), Filterable {
    private var listener: OnClickCopyListener? = null
    private var listToMain: MutableList<ListTo> = mutableListOf()
    private var listToFilter: List<ListTo> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCopy {
       return ViewHolderCopy(parent)
    }

    override fun onBindViewHolder(holder: ViewHolderCopy, position: Int) {
       val itemView = holder.itemView
        val model = listToMain[position]
        itemView.tvText.text = model.emp_name
        itemView.btnCard.setOnClickListener { listener!!.onClickListCopyTo(model) }
    }

    override fun getItemViewType(position: Int): Int { return position }

    override fun getItemCount(): Int { return listToMain.size }

    fun setData(model: List<ListTo>){
        listToMain = ArrayList(model)
        listToFilter = ArrayList(model)
        notifyDataSetChanged()
    }

    fun setOnClickListener(listener: OnClickCopyListener){
        this.listener = listener
    }
    interface OnClickCopyListener{fun onClickListCopyTo(model: ListTo) }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun convertResultToString(resultValue: Any): String {
                return (resultValue as ListTo).emp_name
            }

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                val modelListTo : ArrayList<ListTo> = ArrayList()
                if (constraint != null) {
                    for (model in listToFilter) {
                        if (model.emp_name.toLowerCase(Locale.ROOT).contains(constraint.toString().toLowerCase(Locale.ROOT))) {
                            modelListTo.add(model)
                        }
                    }
                    filterResults.values = modelListTo
                    filterResults.count = modelListTo.size
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                listToMain.clear()
                if (results != null && results.count > 0) {
                    for (obj in results.values as List<*>) {
                        if (obj is ListTo) {
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

class ViewHolderCopy(itemView: View): RecyclerView.ViewHolder(itemView){
    constructor(parent: ViewGroup) : this(LayoutInflater.from(parent.context).inflate(R.layout.list_view_row,parent, false))
}