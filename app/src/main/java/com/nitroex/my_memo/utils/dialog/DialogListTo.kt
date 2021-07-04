package com.nitroex.my_memo.utils.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nitroex.memo.my_memo.R
import com.nitroex.my_memo.ui.memo_create.model.ListTo
import com.nitroex.my_memo.utils.adapter.AddListToAdapter
import kotlinx.android.synthetic.main.view_title_head.view.*

class DialogListTo: DialogFragment(), AddListToAdapter.OnClickListListener{
    private var listener: OnCallbackListener? = null
    private var adapter = AddListToAdapter()
    private lateinit var dialogList: DialogListTo
    private lateinit var v: View
    private lateinit var ctx: Context
    private lateinit var rvList: RecyclerView
    private lateinit var searchView: SearchView
    private var strTitle = ""

    interface OnCallbackListener { fun onAddListTo(model: ListTo?)  }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogFullScreen)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        v = inflater.inflate(R.layout.dialog_list_to, container, false)
        v.tvHeadTitle.text = strTitle; //v.tvHeadTitle.isSelected = true
        v.btnHeadBack.setOnClickListener { dismiss() }
        rvList = v.findViewById(R.id.rvList)
        searchView = v.findViewById(R.id.searchView)

        setApproveList()

        return v
    }

    fun setView(title: String, fragmentManager: FragmentManager?): DialogListTo? {
        dialogList = DialogListTo()
        if (fragmentManager != null) dialogList.show(fragmentManager, "DialogAddPeople")
        dialogList.strTitle = title

        return dialogList
    }

    private fun setApproveList() {
        adapter = AddListToAdapter()
        adapter.setOnClickListener(this)
        rvList.layoutManager = LinearLayoutManager(activity)
        rvList.adapter = adapter
    }

    fun setRvList(model: List<ListTo>){
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })
        adapter.setData(model)
    }

    override fun onClickList(model: ListTo) {
        listener!!.onAddListTo(model)
        dismiss()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.ctx = context
        this.listener = activity as OnCallbackListener?
    }



}