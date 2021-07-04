package com.nitroex.my_memo.utils.attachFile

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.nitroex.my_memo.BaseActivity
import com.nitroex.memo.my_memo.R
import com.nitroex.my_memo.utils.Configs
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_show_pic_full_screen.*
import java.io.File

class ShowPicFullScreenNoAniActivity : BaseActivity() {

    private val url by lazy { intent.getStringExtra(Configs.IMAGE_URL_KEY) }
    private val isPartUrl by lazy { intent.getBooleanExtra("isUrl",false) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_pic_full_screen)
        setStatusBarColor(ContextCompat.getColor(this, R.color.gray), window)

        if (isPartUrl) {
            Glide.with(this)
                .asBitmap()
                .load(url)
                .override(1080)
                .into(detailImage)
        }else{
            Glide.with(this)
                .asBitmap()
                .load(File(url))
                .override(1080)
                .into(detailImage)
        }
    }
}
