package com.nitroex.my_memo.utils.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nitroex.memo.my_memo.R
import com.nitroex.my_memo.utils.Configs
import com.nitroex.my_memo.utils.model.EmpMenu
import kotlinx.android.synthetic.main.list_main_menu.view.*

class MainMenuAdapter(private val context: Context, menuList: List<EmpMenu>) : RecyclerView.Adapter<MainMenuViewHolder>() {
    private var menuLists: List<EmpMenu> = menuList
    private var listener: OnMenuClickListener? = null
    private var numBadge = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainMenuViewHolder {
        return MainMenuViewHolder(parent)
    }

    override fun onBindViewHolder(holder: MainMenuViewHolder, position: Int) {
        val itemView = holder.itemView

        val menu: EmpMenu = menuLists[position]
        itemView.tvMenuName.text = menu.menu_name //set menu name

        when (menu.menu_id) {
            Configs.MenuProfile -> {
                itemView.tvMenuName.text = context.getString(R.string.my_profile)
                itemView.ivProfile.setImageResource(R.drawable.mainmenu_myprofile)
                if (numBadge==0) { itemView.ivDotRed.visibility = View.GONE
                }else{ itemView.ivDotRed.visibility = View.VISIBLE }
            }
            Configs.MenuCreateMemo -> {
                itemView.tvMenuName.text = context.getString(R.string.create_memo)
                itemView.ivProfile.setImageResource(R.drawable.mainmenu_creatememo)
            }
            Configs.MenuDraftMemo -> {
                itemView.tvMenuName.text = context.getString(R.string.draft_memo)
                itemView.ivProfile.setImageResource(R.drawable.mainmenu_draftmemo)
            }
            Configs.MenuStatusMemo -> {
                itemView.tvMenuName.text = context.getString(R.string.memo_status)
                itemView.ivProfile.setImageResource(R.drawable.mainmenu_memostatus)
            }
            Configs.MenuToDoList -> {
                itemView.tvMenuName.text = context.getString(R.string.action_list)
                itemView.ivProfile.setImageResource(R.drawable.mainmenu_actionlist)
            }
            Configs.MenuFavoriteMemo -> {
                itemView.tvMenuName.text = context.getString(R.string.favorites_form)
                itemView.tvMenuName.text = context.getString(R.string.favorites_form)
                itemView.ivProfile.setImageResource(R.drawable.favoritememobuttonselected)
            }
        }

        itemView.btnMenu.setOnClickListener {
            listener!!.onClickMenu(position, menu.menu_id, itemView.tvMenuName.text.toString())
        }
    }

    fun setItem(badge: Int) {
        this.numBadge = badge
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int { return position }
    override fun getItemCount(): Int { return menuLists.size }

    fun setOnClickListener(listener: OnMenuClickListener?) {
        this.listener = listener
    }
    interface OnMenuClickListener { fun onClickMenu(position: Int, menuId: Int, menuName: String) }

}

class MainMenuViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    constructor(parent: ViewGroup) : this(LayoutInflater.from(parent.context).inflate(R.layout.list_main_menu, parent, false))
}