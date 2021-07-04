package com.nitroex.my_memo.ui

import android.content.Context
import android.os.Bundle
import com.nitroex.my_memo.BaseActivity
import com.nitroex.memo.my_memo.R
import com.nitroex.my_memo.utils.Configs
import com.nitroex.my_memo.utils.listener.OnDialogFullCallbackListener
import com.akexorcist.localizationactivity.core.LocalizationApplicationDelegate
import com.google.firebase.firestore.BuildConfig
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class SplashScreenActivity : BaseActivity() {
    private val localizationDelegate = LocalizationApplicationDelegate()
    override fun attachBaseContext(newBase: Context) {
        localizationDelegate.setDefaultLanguage(newBase, Configs.Thai) //Set Default Language
        super.attachBaseContext(localizationDelegate.attachBaseContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        getConfigFirebase()
    }

    private fun getConfigFirebase() {
        val db = FirebaseFirestore.getInstance()
        FirebaseFirestore.setLoggingEnabled(false)

        val strDoc = when (BuildConfig.BUILD_TYPE){
            "debug" -> "CheckConfigsMyMemoProd"
            "release" -> "CheckConfigsMyMemoProd"
            "demo_dev" -> "CheckConfigsMyMemoDemoDev"
            "demo_prod" -> "CheckConfigsMyMemoDemoProd"
            else -> ""
        }
        val docRef = db.collection("AndroidConfigs").document(strDoc)
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                isLog("FireStoreConfigs", "BuildType: "+ BuildConfig.BUILD_TYPE +" "+document!!.data.toString())

                val isForceUpdate = document.data!!["isForceUpdate"] as Boolean
                val isUrlApiService = document.data!!["isUrlApiService"].toString()
                val isUrlDownload = document.data!!["isUrlDownload"].toString()
                val version = document.data!!["version"].toString()

                setSharePreSystem(this, Configs.URL_API, isUrlApiService)
                if (getSharePreSystem(this, Configs.DeviceToken,"").isEmpty())
                    setSharePreSystem(this, Configs.DeviceToken, UUID.randomUUID().toString())

                checkAppLastedVersion(version, isForceUpdate, isUrlDownload)
            } else {
                showDialogAlertCloseApp(false, getString(R.string.try_again_connect_server))
            }
        }
    }

    private fun checkAppLastedVersion(version: String, isForceUpdate: Boolean, isUrlDownload: String ) {
        if (isVersionNameMatch(version)) { // Version Is Match
            endCheckConfigs()
        } else {
            showDialogDefaultListener(getString(R.string.titile_up), getString(R.string.titile_tv_up), !isForceUpdate,
                object : OnDialogFullCallbackListener {
                    override fun onSubmit() { openActivityIntentBrowser(this@SplashScreenActivity, isUrlDownload) }
                    override fun onCancel() { endCheckConfigs() }
                    override fun onDismiss() { finish() }
                })
        }
    }

    private fun endCheckConfigs(){
        openActivityFinish(this, LoginActivity::class.java)
        if (isLogin()) { overridePendingTransition(R.anim.fade_in_real, R.anim.fade_out_real) }
    }
}