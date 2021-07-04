package com.nitroex.my_memo.utils.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.app.Activity
import android.widget.ArrayAdapter
import com.nitroex.memo.my_memo.R
import com.nitroex.my_memo.utils.model.Company

class CompanyAdapter(private val mContext: Context, mLayoutResourceId: Int, company: ArrayList<Company>) : ArrayAdapter<Company>(mContext, mLayoutResourceId) {

    private val model: ArrayList<Company> = company
    private var layout: Int = mLayoutResourceId

    override fun getCount(): Int { return model.size }
    override fun getItem(position: Int): Company? { return model[position] }
    override fun getItemId(position: Int): Long { return position.toLong() }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var convertView = view
        try {
            if (convertView == null) {
                val inflater = (mContext as Activity).layoutInflater
                convertView = inflater.inflate(layout, parent, false)
            }
            val company = getItem(position)
            val name = convertView!!.findViewById(R.id.tvText) as TextView
            name.text = company!!.company_name
        } catch (e: Exception) { e.printStackTrace() }
        return convertView!!
    }

}