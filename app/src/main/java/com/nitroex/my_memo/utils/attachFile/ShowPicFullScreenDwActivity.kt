package com.nitroex.my_memo.utils.attachFile

import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.ChangeImageTransform
import android.transition.Fade
import android.transition.TransitionSet
import android.view.WindowManager
import androidx.core.transition.doOnEnd
import com.nitroex.my_memo.BaseActivity
import com.nitroex.memo.my_memo.R
import com.nitroex.my_memo.utils.Configs
import kotlinx.android.synthetic.main.activity_show_pic_full_screen.*

class ShowPicFullScreenDwActivity : BaseActivity() {

    private val url by lazy { intent.getIntExtra(Configs.IMAGE_URL_KEY,0) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_pic_full_screen)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        supportPostponeEnterTransition()
        detailImage.transitionName = url.toString()
        detailImage.loadDrawable(url) {
            supportStartPostponedEnterTransition()
        }
        window.sharedElementEnterTransition = TransitionSet()
            .addTransition(ChangeImageTransform())
            .addTransition(ChangeBounds())
            .apply {
                doOnEnd { detailImage.loadDrawable(url) }
            }
        window.enterTransition = Fade().apply {
            excludeTarget(android.R.id.statusBarBackground, true)
            excludeTarget(android.R.id.navigationBarBackground, true)
            excludeTarget(R.id.action_bar_container, true)
        }
    }
}
