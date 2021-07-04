package com.nitroex.my_memo.utils.attachFile

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.nitroex.my_memo.BaseActivity
import com.nitroex.memo.my_memo.R
import com.nitroex.my_memo.utils.attachFile.adapter.AttachFileAdapter
import com.nitroex.my_memo.utils.attachFile.adapter.GalleryAdapter
import com.nitroex.my_memo.utils.attachFile.adapter.GalleryVPagerAdapter
import com.nitroex.my_memo.utils.attachFile.model.AttachFile
import kotlinx.android.synthetic.main.activity_gallary.*

class GalleryActivity : BaseActivity(), GalleryAdapter.OnClickCardListener {
    private lateinit var model: ArrayList<AttachFile>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallary)
        setStatusBarColor(ContextCompat.getColor(this, R.color.grayDark), window)

        try {
            model = intent.getSerializableExtra("gallery_list") as ArrayList<AttachFile>
            val index = intent.getIntExtra("index_select",0)

            for(i in model.indices){ // ลบที่เป็นช่องเพิ่มรูปออก
                if (model[i].type_id == AttachFileAdapter.ADD) { model.removeAt(i) }
            }

            val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            rvPicPreview.layoutManager = layoutManager
            val previewAdapter = GalleryAdapter(this)
            rvPicPreview.adapter = previewAdapter
            previewAdapter.setOnClickCardListener(this)
            previewAdapter.setItem(model)
            rvPicPreview.scrollToPosition(index)

            val showAdapter = GalleryVPagerAdapter(this)
            vpPicShow.adapter = showAdapter
            vpPicShow.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            showAdapter.setItem(model)
            vpPicShow.setCurrentItem(index,false)

            //onPageSelected
            vpPicShow.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    rvPicPreview.scrollToPosition(position)
                }
            })
        } catch (e: Exception) {
            finish()
        }
    }

    override fun onClickCard(position: Int) {
        vpPicShow.setCurrentItem(position,true)
    }

}
