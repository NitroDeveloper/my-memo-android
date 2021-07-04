package com.nitroex.my_memo

import android.app.Activity
import android.app.Dialog
import android.content.*
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.nitroex.my_memo.service.APICode
import com.nitroex.my_memo.ui.SplashScreenActivity
import com.nitroex.my_memo.ui.memo_create.model.ListTo
import com.nitroex.my_memo.utils.Configs
import com.nitroex.my_memo.utils.attachFile.ShowPicFullScreenActivity
import com.nitroex.my_memo.utils.attachFile.model.AttachFile
import com.nitroex.my_memo.utils.listener.OnDialogCallbackListener
import com.nitroex.my_memo.utils.listener.OnDialogDismissListener
import com.nitroex.my_memo.utils.listener.OnDialogFullCallbackListener
import com.nitroex.my_memo.utils.localization.LocalizationActivity
import com.nitroex.my_memo.utils.model.CMLogin
import com.nitroex.my_memo.utils.model.CMModel
import com.nitroex.my_memo.utils.model.Company
import com.nitroex.my_memo.utils.model.EmpMenu
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.github.gcacace.signaturepad.views.SignaturePad
import com.google.firebase.BuildConfig
import com.google.gson.Gson
import com.nitroex.memo.my_memo.R
import kotlinx.android.synthetic.main.dialog_loading.*
import kotlinx.android.synthetic.main.view_block_no_data.*
import kotlinx.android.synthetic.main.view_icon_bottom.*
import kotlinx.android.synthetic.main.view_title_head.*
import org.apache.maven.artifact.versioning.DefaultArtifactVersion
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset
import kotlin.system.exitProcess

open class BaseActivity : LocalizationActivity() {
    private lateinit var sharePreUser: SharedPreferences
    private lateinit var sharePreSystem: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        APICode.newInstance(this)
    }

    // sharePreUser //
    fun setSharePreUser(context: Context, key: String, value: String) {
        sharePreUser = context.getSharedPreferences(Configs.PREFS_USER, Context.MODE_PRIVATE)
        sharePreUser.edit().putString(key, value).apply()
    }
    fun getSharePreUser(context: Context, key: String, def: String): String {
        sharePreUser = context.getSharedPreferences(Configs.PREFS_USER, Context.MODE_PRIVATE)
        return sharePreUser.getString(key, def)!!
    }
    fun setSharePreUser(context: Context, key: String, value: Boolean) {
        sharePreUser = context.getSharedPreferences(Configs.PREFS_USER, Context.MODE_PRIVATE)
        sharePreUser.edit().putBoolean(key, value).apply()
    }
    fun getSharePreUser(context: Context, key: String, def: Boolean): Boolean {
        sharePreUser = context.getSharedPreferences(Configs.PREFS_USER, Context.MODE_PRIVATE)
        return sharePreUser.getBoolean(key, def)
    }
    fun clearSharePreUser() {
        sharePreUser = getSharedPreferences(Configs.PREFS_USER, Context.MODE_PRIVATE)
        return sharePreUser.edit().clear().apply()
    }
    fun clearSharePreUser(context: Context) {
        sharePreUser = context.getSharedPreferences(Configs.PREFS_USER, Context.MODE_PRIVATE)
        return sharePreUser.edit().clear().apply()
    }
    // sharePreUser //

    // sharePreSystem //
    fun setSharePreSystem(context: Context, key: String, value: String) {
        sharePreSystem = context.getSharedPreferences(Configs.PREFS_SYSTEM, Context.MODE_PRIVATE)
        sharePreSystem.edit().putString(key, value).apply()
    }
    fun getSharePreSystem(context: Context, key: String, def: String): String {
        sharePreSystem = context.getSharedPreferences(Configs.PREFS_SYSTEM, Context.MODE_PRIVATE)
        return sharePreSystem.getString(key, def)!!
    }
    fun setSharePreSystem(context: Context, key: String, value: Boolean) {
        sharePreSystem = context.getSharedPreferences(Configs.PREFS_SYSTEM, Context.MODE_PRIVATE)
        sharePreSystem.edit().putBoolean(key, value).apply()
    }
    fun getSharePreSystem(context: Context, key: String, def: Boolean): Boolean {
        sharePreSystem = context.getSharedPreferences(Configs.PREFS_SYSTEM, Context.MODE_PRIVATE)
        return sharePreSystem.getBoolean(key, def)
    }
    fun setSharePreSystem(context: Context, key: String, value: Int) {
        sharePreSystem = context.getSharedPreferences(Configs.PREFS_SYSTEM, Context.MODE_PRIVATE)
        sharePreSystem.edit().putInt(key, value).apply()
    }
    fun getSharePreSystem(context: Context, key: String, def: Int): Int {
        sharePreSystem = context.getSharedPreferences(Configs.PREFS_SYSTEM, Context.MODE_PRIVATE)
        return sharePreSystem.getInt(key, def)
    }
    fun clearSharePreSystem() {
        sharePreSystem = getSharedPreferences(Configs.PREFS_SYSTEM, Context.MODE_PRIVATE)
        return sharePreSystem.edit().clear().apply()
    }
    fun clearSharePreSystem(context: Context) {
        sharePreSystem = context.getSharedPreferences(Configs.PREFS_SYSTEM, Context.MODE_PRIVATE)
        return sharePreSystem.edit().clear().apply()
    }
    // sharePreSystem //

    /////////////////////////////////// Start Activity Function /////////////////////////////////////

    fun openActivity(cls: Class<*>) {
        startActivity(Intent(this, cls))
    }

    fun openActivityForResult(cls: Class<*>, request: Int) {
        val intent = Intent(this, cls)
        startActivityForResult(intent, request)
    }

    fun openActivityWithBundleForResult(cls: Class<*>, bundle: Bundle, request: Int) {
        val intent = Intent(this, cls)
        intent.putExtra(Configs.Data, bundle)
        startActivityForResult(intent, request)
    }

    fun openActivityWithBundle(cls: Class<*>, bundle: Bundle) {
        val intent = Intent(this, cls)
        intent.putExtra(Configs.Data, bundle)
        startActivity(intent)
    }

    fun openActivityFinish(cls: Class<*>) {
        val intent = Intent(this, cls)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    fun openActivityFinish(context: Context, cls: Class<*>) {
        val intent = Intent(context, cls)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        context.startActivity(intent)
        finish()
    }
    fun openActivityFinishWithBundle(cls: Class<*>, bundle: Bundle) {
        val intent = Intent(this, cls)
        intent.putExtra(Configs.Data, bundle)
        startActivity(intent)
        finishAffinity()
    }

    fun openActivityIntentBrowser(context: Context, isUrlDownload: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(isUrlDownload)
        context.startActivity(intent)
    }

    fun openActivityPictureFullScreen(context: Context?, url: String, ivImage: ImageView?) {
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(context as Activity, ivImage!!, ivImage.transitionName).toBundle()
        Intent(context, ShowPicFullScreenActivity::class.java)
                .putExtra(Configs.IMAGE_URL_KEY, url)
                .let { startActivityForResult(it, Configs.REQUEST_PIC_FULLSCREEN, options) }
    }
    /////////////////////////////////// Start Activity Function /////////////////////////////////////

    //////////////////////////////////////////////////////// Set Dialog Function  ////////////////////////////////////////////////////////

    open fun showDialogConfirm(listener: OnDialogCallbackListener, message: String?) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_confirm)
        dialog.window!!.setDimAmount(0.4f)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
        val tvContent = dialog.findViewById<TextView>(R.id.tvContent)
        val btnSubmit = dialog.findViewById<LinearLayout>(R.id.btnSubmit)
        val btnClose = dialog.findViewById<ImageView>(R.id.btnClose)

        tvTitle.text = getString(R.string.confirm)
        tvContent.text = message

        btnSubmit.setOnClickListener {
            dialog.dismiss()
            listener.onSubmit()
        }
        btnClose.setOnClickListener {
            dialog.dismiss()
            listener.onCancel()
        }
        dialog.show()
    }

    open fun showDialogAlertFinish(context: Context, isSuccess: Boolean, message: String?) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_alert)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val blockCard = dialog.findViewById<LinearLayout>(R.id.blockCard)
        val tvContent = dialog.findViewById<TextView>(R.id.tvContent)
        val ivIcon = dialog.findViewById<ImageView>(R.id.ivIcon)

        tvContent.text = message
        if (isSuccess) { ivIcon.setBackgroundResource(R.drawable.success)
        } else { ivIcon.setBackgroundResource(R.drawable.error) }

        blockCard.setOnClickListener { dialog.dismiss() }
        dialog.setOnDismissListener { (context as Activity).finish()}

        dialog.show()
    }

    open fun showDialogAlertCloseApp(isSuccess: Boolean, message: String?) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_alert)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val blockCard = dialog.findViewById<LinearLayout>(R.id.blockCard)
        val tvContent = dialog.findViewById<TextView>(R.id.tvContent)
        val ivIcon = dialog.findViewById<ImageView>(R.id.ivIcon)

        tvContent.text = message
        if (isSuccess) { ivIcon.setBackgroundResource(R.drawable.success)
        } else { ivIcon.setBackgroundResource(R.drawable.error) }

        blockCard.setOnClickListener { dialog.dismiss() }
        dialog.setOnDismissListener { exitApp(this) }

        dialog.show()
    }

    open fun showDialogWarningAlert(message: String?) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_alert)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val blockCard = dialog.findViewById<LinearLayout>(R.id.blockCard)
        val tvContent = dialog.findViewById<TextView>(R.id.tvContent)
        val ivIcon = dialog.findViewById<ImageView>(R.id.ivIcon)

        tvContent.text = message
        ivIcon.setBackgroundResource(R.drawable.warning)

        blockCard.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    open fun showDialogAlert(isSuccess: Boolean, message: String?) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_alert)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val blockCard = dialog.findViewById<LinearLayout>(R.id.blockCard)
        val tvContent = dialog.findViewById<TextView>(R.id.tvContent)
        val ivIcon = dialog.findViewById<ImageView>(R.id.ivIcon)

        tvContent.text = message
        if (isSuccess) { ivIcon.setBackgroundResource(R.drawable.success)
        } else { ivIcon.setBackgroundResource(R.drawable.error) }

        blockCard.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    open fun showDialogAlert(context: Context, isSuccess: Boolean, message: String?) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_alert)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val blockCard = dialog.findViewById<LinearLayout>(R.id.blockCard)
        val tvContent = dialog.findViewById<TextView>(R.id.tvContent)
        val ivIcon = dialog.findViewById<ImageView>(R.id.ivIcon)

        tvContent.text = message
        if (isSuccess) { ivIcon.setBackgroundResource(R.drawable.success)
        } else { ivIcon.setBackgroundResource(R.drawable.error) }

        blockCard.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    open fun showDialogAlertListener(isSuccess: Boolean, message: String?, listener: OnDialogDismissListener) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_alert)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val blockCard = dialog.findViewById<LinearLayout>(R.id.blockCard)
        val tvContent = dialog.findViewById<TextView>(R.id.tvContent)
        val ivIcon = dialog.findViewById<ImageView>(R.id.ivIcon)

        tvContent.text = message
        if (isSuccess) { ivIcon.setBackgroundResource(R.drawable.success)
        } else { ivIcon.setBackgroundResource(R.drawable.error) }

        blockCard.setOnClickListener { dialog.dismiss() }
        dialog.setOnDismissListener { listener.onDismiss() }
        dialog.show()
    }

    open fun showDialogDefaultKickUser(context: Context, message: String?) {
        val dialog = AlertDialog.Builder(this).create()
        dialog.setTitle(R.string.confirm)
        dialog.setMessage(message)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.close)) { d, _ ->
            exitApp(context as Activity)
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.confirm)) { d, _ ->
            openActivityFinish(SplashScreenActivity::class.java)
        }
        dialog.show()
    }

    fun showDialogDefaultListener(title: String, msg: String, isCancel: Boolean, listener: OnDialogFullCallbackListener){
        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setTitle(title)
        alertDialog.setMessage(msg)
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok)) { dialog, i ->
            listener.onSubmit()
        }
        if (isCancel) {
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel)) { dialog, which ->
                listener.onCancel()
            }
        }
        alertDialog.setOnDismissListener { listener.onDismiss() }
        alertDialog.show()
    }

    fun showDialogReturnRefreshActivity(strJson: String) {
        val model = Gson().fromJson(strJson, CMModel::class.java)
        showDialogAlertListener(true, model.message,
                object : OnDialogDismissListener {
                    override fun onDismiss() {
                        returnRefreshActivityFinish()
                    }
                })
    }

    fun loadingDialog(context: Context, isGray: Boolean): Dialog {
        val pDialog = Dialog(context)
        pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        pDialog.setContentView(R.layout.dialog_loading)
        pDialog.setCanceledOnTouchOutside(false)
        pDialog.window!!.setDimAmount(0.0f)
        pDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        if (isGray) {
            setViewVisible(pDialog.ivLoadingG, true)
            setViewVisible(pDialog.ivLoadingW, false)
        }else{
            setViewVisible(pDialog.ivLoadingG, false)
            setViewVisible(pDialog.ivLoadingW, true)
        }

        return pDialog
    }

    //////////////////////////////////////////////////////// Set Dialog Function  ////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////// Check Value Function  ///////////////////////////////////////////////////////

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connectivityManager.allNetworkInfo
        for (i in info.indices)
            if (info[i].state == NetworkInfo.State.CONNECTED) {
                return true
            }
        return false
    }

    fun isBuildDebug(): Boolean{
        return BuildConfig.DEBUG
    }

    fun isVersionNameMatch(verConfig: String): Boolean {
        val buildVersion = DefaultArtifactVersion(getVersionApp())
        val lastVersion = DefaultArtifactVersion(verConfig)

        val alert = if (buildVersion < lastVersion) { "Version name not match !!"
        }else{ "Version name is latest version !!" }
        isLog("CheckVersion", "buildVersion : $buildVersion, lastVersion : $lastVersion = $alert")

        return buildVersion >= lastVersion
    }

    fun isLogin(): Boolean{
        return getSharePreUser(this, Configs.IsLogin, false)
    }

    fun checkTextIsNotEmptyAlert(strText: String, strAlert: String, isSuccess: Boolean): Boolean {
        if (strText == null || strText.isEmpty() || strText == "null") {
            showDialogAlert(isSuccess, strAlert)
            return false
        }
        return true
    }

    fun checkTextIsNotEmpty(strText: String?): Boolean {
        return !(strText == null || strText.isEmpty() || strText == "null")
    }

    fun isValidateEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target!!).matches()
    }

    //////////////////////////////////////////////////////// Check Value Function  ////////////////////////////////////////////////////

    //////////////////////////////////////////////////////// Function Set Picture /////////////////////////////////////////////////////

    fun setPicGlideReSize(context: Context, file: File, imageView: ImageView) {
        Glide.with(context).asBitmap().load(file).override(800).into(imageView)
    }

    fun setPicGlideReSize(context: Context, file: Int, imageView: ImageView) {
        Glide.with(context).asBitmap().load(file).override(800).into(imageView)
    }

    fun setPicGlideReSize(context: Context, file: String, imageView: ImageView) {
        Glide.with(context)
                .asBitmap()
                .load(file)
                .override(800)
                .apply(RequestOptions().placeholder(R.drawable.placeholder_gallary))
                .into(imageView)
    }

    fun setPicGlideReSizeNoCache(context: Context, file: String, imageView: ImageView) {
        Glide.with(context)
                .asBitmap()
                .load(file)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .override(800)
                .into(imageView)
    }

    fun setPicGlide(context: Context, file: ByteArray, imageView: ImageView) {
        Glide.with(context).asBitmap().load(file).into(imageView)
    }

    fun setPicGlide(context: Context, file: File, imageView: ImageView) {
        Glide.with(context).asBitmap().load(file).into(imageView)
    }

    fun setPicGlide(context: Context, file: Int, imageView: ImageView) {
        Glide.with(context).asBitmap().load(file).into(imageView)
    }

    fun setPicGlide(context: Context, file: String, imageView: ImageView) {
        Glide.with(context).asBitmap().load(file).into(imageView)
    }

    fun setPicGlideProfile(context: Context, url: String, imageView: ImageView) {
        Glide.with(context)
                .asBitmap()
                .load(url)
                .centerCrop()
                .apply(RequestOptions().placeholder(R.drawable.ic_profile))
                .error(R.drawable.ic_profile).into(imageView)
    }

    //////////////////////////////////////////////////////// Function Set Picture /////////////////////////////////////////////////////

    //////////////////////////////////////////////////////// Function Application /////////////////////////////////////////////////////

    fun getLanguage(): String {
        return getCurrentLanguage().language
    }

    fun getVersionApp(): String {
        val pInfo = packageManager.getPackageInfo(packageName, 0)
        return pInfo.versionName
    }

    fun exitApp(context: Activity){
        context.finishAffinity()
        exitProcess(0)
    }

    fun isLog(tag: String?, mag: String) {
        if (isBuildDebug()) {
            Log.e("->>command:$tag", mag)
        }
    }

    fun isLogDebug(tag: String?, mag: String) {
        if (isBuildDebug()) {
            Log.d("->>command:$tag", mag)
        }
    }

    fun setViewShow(isShow: View, isHide: View) {
        isShow.visibility = View.VISIBLE
        isHide.visibility = View.GONE
    }

    fun setViewVisible(view: View, isShow: Boolean) {
        if (isShow) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }

    fun setViewInVisible(view: View, isShow: Boolean) {
        if (isShow) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.INVISIBLE
        }
    }

    fun getHintToTrim(tv: TextView): String{ return tv.hint.trim().toString() }
    fun getHintToTrim(edt: EditText): String{ return edt.hint.trim().toString() }
    fun getTextToTrim(tv: TextView): String{ return tv.text.trim().toString() }
    fun getTextToTrim(edt: EditText): String{ return edt.text.trim().toString() }
    fun getTextToTrim(txt: String): String{ return txt.trim() }

    fun getStringJsonObj(jsonObject: JSONObject, str: String): String{
        return try { jsonObject.getString(str)
        } catch (e: Exception) { "" }
    }
    fun getBooleanJsonObj(jsonObject: JSONObject, str: String): Boolean{
        return try { jsonObject.getBoolean(str)
        } catch (e: Exception) { false }
    }

    fun isJsonType(str: String): Boolean {
        return str.contains("\"command\"")
    }

    fun returnBundleRefreshActivity(bundle: Bundle) {
        val data = Intent()
        data.putExtra(Configs.Data, bundle)
        if (parent==null) setResult(RESULT_OK, data)
        else parent.setResult(RESULT_OK, data)
    }

    fun returnRefreshActivity() {
        val data = Intent()
        if (parent==null) setResult(RESULT_OK, data)
        else parent.setResult(RESULT_OK, data)
    }

    fun returnRefreshActivityFinish() {
        val data = Intent()
        if (parent==null) setResult(RESULT_OK, data)
        else parent.setResult(RESULT_OK, data)
        finish()
    }

    fun getBundleFromIntent(activity: Activity): Bundle? {
        return try { activity.intent.extras!!.getBundle(Configs.Data)
        } catch (e: Exception) { null }
    }

    fun getStringFromBundle(key: String, intent: Intent): String {
        return try { intent.extras!!.getBundle(Configs.Data)!!.getString(key, "")
        } catch (e: Exception) { "" }
    }

    fun setTitleAppBar(str: String) {
        tvHeadTitle.text = str
    }

    fun setTitleAppBar(bundle: Bundle) {
        tvHeadTitle.text = bundle.getString("menuName")
    }

    fun getTitleAppBar(): String {
        return tvHeadTitle.text.toString()
    }

    fun setTextEmptyAddDash(txt: String?): String{
        return try {
            if(txt!!.isEmpty() || txt == "null") { "-"
            }else{ txt }
        } catch (e: Exception) { "-" }
    }

    fun setTextEmptyAddDef(txt: String?, txtDef: String?): String{
        return try {
            if(txt!!.isEmpty() || txt == "null") { txtDef!!
            }else{ txt }
        } catch (e: Exception) { txtDef!! }
    }

    fun setTextApiNoData( message: String) {
        tvNoData.text = message
    }

    fun setHideFabOnScroll(rvList: RecyclerView) {
        val scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        val scaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_down)
        rvList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && btnFabIcon.visibility == View.VISIBLE) {
                    btnFabIcon.startAnimation(scaleDown)
                    setViewVisible(btnFabIcon, false)

                } else if (dy < 0 && btnFabIcon.visibility == View.GONE) {
                    btnFabIcon.startAnimation(scaleUp)
                    setViewVisible(btnFabIcon, true)
                }
            }
        })
    }

    fun setHideFabOnScroll(scrollView: NestedScrollView) {
        val scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        val scaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_down)
        scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY > oldScrollY && btnFabIcon.visibility == View.VISIBLE) {
                btnFabIcon.startAnimation(scaleDown)
                setViewVisible(btnFabIcon, false)

            } else if (scrollY < oldScrollY && btnFabIcon.visibility == View.GONE) {
                btnFabIcon.startAnimation(scaleUp)
                setViewVisible(btnFabIcon, true)
            }
        })
    }

    fun setStatusBarColor(color: Int, window: Window) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = color
    }

    fun setStatusBarTransparent() {
        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = Color.TRANSPARENT
    }

    fun setClearSearchView(searchView: SearchView) {
        searchView.setQuery("", false)
        searchView.clearFocus()
    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun loadJSONFromAsset(jsonFile: String): String? {
        return try {
            val `is`: InputStream = assets.open(jsonFile)
            val size: Int = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()

            val charset: Charset = Charsets.UTF_8
            String(buffer, charset)

        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
    }

    fun setIntentViewExport(link: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.parse(link), "application/pdf")
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        startActivity(intent)
    }

    fun getViewDecor() : View {
        return window.decorView.findViewById(android.R.id.content)
    }

    private fun saveActive() { setSharePreSystem(this, Configs.isActive, true) }
    private fun saveUnActive() { setSharePreSystem(this, Configs.isActive, false) }

    //////////////////////////////////////////////////////// Function Application /////////////////////////////////////////////////////

    //////////////////////////////////////// Get API Model /////////////////////////////////////////

    fun getDataLogin(): CMLogin {
        return Gson().fromJson(getSharePreUser(this, Configs.JsonLogin, ""), CMLogin::class.java)
    }
    fun getDataUser(): CMLogin {
        return Gson().fromJson(getSharePreUser(this, Configs.JsonLogin, ""), CMLogin::class.java)
    }
    fun getDataCompany(): ArrayList<Company> {
        return Gson().fromJson(getSharePreUser(this, Configs.JsonLogin, ""), CMLogin::class.java).company
    }
    fun getDataMenu(): List<EmpMenu> {
        val company = getDataCompany()
        for (i in company.indices)
            if (company[i].com_id_pk == getCompanyId())
                return company[i].emp_menu!!
        return listOf()
    }

    //////////////////////////////////////// Get API Model /////////////////////////////////////////

    ///////////////////////////////////////////////////////// Function App Memo //////////////////////////////////////////////////////

    fun getEmpName(): String{ return getDataLogin().data[0].emp_name }
    fun getEmpPosition(): String{ return getDataLogin().data[0].emp_position }
    fun getCompanyId(): String{ return getSharePreUser(this, Configs.CompanyID, "") }
    fun getCompanyName(): String{ return getSharePreUser(this, Configs.CompanyName, "") }
    fun getSignatureSP(): String{ return getSharePreUser(this, Configs.EmpSignature, "") }

    fun setBlockSignature(str: String, viewBlock: View, imageView: ImageView){
        if (str.isNotEmpty()) {
            setPicGlide(this, convertSignatureStringToByte(str), imageView)
            setViewVisible(viewBlock, true)
        }else{
            setViewVisible(viewBlock, false)
        }
    }

    fun convertSignatureStringToByte(strSignature: String): ByteArray {
        val start: Int = strSignature.indexOf(',')
        val imageBytes: String = strSignature.substring(start + 1)
        return Base64.decode(imageBytes, Base64.DEFAULT)
    }

    fun checkFormIsNotEmpty(editText: EditText, textAlert: String): Boolean {
        return if (getTextToTrim(editText).isNotEmpty()){ true
        }else{ showDialogAlert(false, textAlert); false }
    }

    fun checkFormIsNotEmpty(editText: EditText): Boolean {
        return if (getTextToTrim(editText).isNotEmpty()){ true
        }else{ showDialogAlert(false, editText.hint.toString()); false }
    }

    fun checkFormIsNotEmpty(text: String, textAlert: String): Boolean {
        return if (text.isNotEmpty()){ true
        }else{ showDialogAlert(false, getString(R.string.please_choose) + textAlert); false }
    }

    fun checkFormIsNotEmpty(list: ArrayList<ListTo>, txt: Int): Boolean {
        return if (list.isNotEmpty()){ true }
        else{ showDialogAlert(false, getString(txt)); false }
    }

    fun checkMemoDetailNotEmpty(edtMeReason: EditText, edtMePurpose: EditText, edtMeSummary: EditText, strCreateByPlatForm: Int): Boolean {
        when (strCreateByPlatForm) {
            Configs.Channel_ID_Web -> { return true }
            Configs.Channel_ID_Mobile -> {
                if (checkFormIsNotEmpty(edtMeReason))
                    if (checkFormIsNotEmpty(edtMePurpose))
                        if (checkFormIsNotEmpty(edtMeSummary))
                            return true
            }
        }
        return false
    }

    fun checkInputTextAttachment(list: ArrayList<AttachFile>, editText: EditText): Boolean {
        return if (list.size > 1){
            if (getTextToTrim(editText).isEmpty()) {
                showDialogAlert(false, editText.hint.toString()); false
            }else{ true }
        }else{
            if (getTextToTrim(editText).isNotEmpty()) {
                showDialogAlert(false, getString(R.string.please_add_attach_file)); false
            }else{ true }
        }
    }

    fun checkRadioButtonIsNotEmpty(isStatus: Int, editText: EditText): Boolean {
        return if (isStatus==1) {
            if (getTextToTrim(editText).isEmpty()) {
                showDialogAlert(false, editText.hint.toString()); false
            }else{ true }
        }else{ true }
    }

    fun setTitleFormById(textView: TextView, formId: String) {
        when (formId) {
              Configs.INTERNAL_FORM_ID -> {
                textView.text = getString(R.string.internal_form)
            } else -> { textView.text = "memo_form_id : $formId"}
        }
    }

    fun getActionNameFromId(strFromID: String): String{
        return when (strFromID) {
            "0" -> {
                getEmpName()
            }
            "1" -> {
                getString(R.string.acting_on_behalf_of)
            }
            "2" -> {
                getString(R.string.acting_for)
            }
            else -> {""}
        }
    }

    fun getConfidentNameFromId(strConfidentID: String): String {
        return when (strConfidentID) {
            "0" -> {
                getString(R.string.none)
            }
            "1" -> {
                getString(R.string.confidential)
            }
            "2" -> {
                getString(R.string.secret)
            }
            "3" -> {
                getString(R.string.top_secret)
            }
            else -> ""
        }
    }

    fun getSpeedLevelNameFromId(strSpeedLevelID: String): String {
        return when (strSpeedLevelID) {
            "0" -> {
                getString(R.string.none)
            }
            "1" -> {
                getString(R.string.priority)
            }
            "2" -> {
                getString(R.string.immediate)
            }
            "3" -> {
                getString(R.string.urgent)
            }
            else -> ""
        }
    }

    open fun showDialogTipsAttachment() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_tips_attachment)
        dialog.window!!.setDimAmount(0.6f)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)

        val btnClose = dialog.findViewById<LinearLayout>(R.id.btnClose)
        btnClose.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    fun setSharePreUserByCompany(empId: String, empComId: String, companyId: String, companyName: String) {
        setSharePreUser(this, Configs.EmpID, empId)
        setSharePreUser(this, Configs.EmpComID, empComId)
        setSharePreUser(this, Configs.CompanyID, companyId)
        setSharePreUser(this, Configs.CompanyName, companyName)
    }

    fun getParagraphArrayToString(list: List<String>): String {
        var detail = ""
        for (i in list.indices){
            detail += if (list[i].isNotEmpty()) {
                "<p>"+list[i]+"</p>"
            }else{
                "<p></p>"
            }
        }
        return detail
    }

    fun setClearParagraphStringToArray(txtDummy: String): List<String> {
        var txt = txtDummy
        var str1 = ""; var str2 = ""; var str3 = ""

        try {
            str1 = txt.substring(0, txt.indexOf("</p>")).replace("<p>","")

            txt = txt.substring(txt.indexOf("</p>"))
            txt = txt.substring(4, txt.length)

            str2 = txt.substring(0, txt.indexOf("</p>")).replace("<p>","")
            txt = txt.substring(txt.indexOf("</p>"))
            txt = txt.substring(4, txt.length)

            str3 = txt.substring(0, txt.indexOf("</p>")).replace("<p>","")
        } catch (e: Exception) { str1=txtDummy; str2=""; str3=""}

        return listOf(str1, str2, str3)
    }

    fun setTextNToBr(text: String): String {
        var dummy = text
        dummy = dummy.replace("\n","<br>")
        dummy = dummy.replace("\n","<br/>")
        return dummy
    }
    fun setTextBrToN(text: String): String {
        var dummy = text
        dummy = dummy.replace("<br>","\n")
        dummy = dummy.replace("<br/>","\n")
        return dummy
    }

    fun setSignatureByBase64(signature: String, mSignaturePad: SignaturePad){
        val start: Int = signature.indexOf(',')
        val imageBytes: String = java.lang.String.valueOf(signature).substring(start + 1)
        val imageByteArray = Base64.decode(imageBytes, Base64.DEFAULT)
        val decodedByte = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
        if (decodedByte != null) { mSignaturePad.signatureBitmap = decodedByte }
    }

    fun setSignatureByBase64(signature: String, imageView: ImageView?){
        val start: Int = signature.indexOf(',')
        val imageBytes: String = java.lang.String.valueOf(signature).substring(start + 1)
        val imageByteArray = Base64.decode(imageBytes, Base64.DEFAULT)
        Glide.with(this).load(imageByteArray).into(imageView!!)
    }

    ///////////////////////////////////// BroadcastReceiver //

    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Configs.NoticeKickUser) {
                showDialogDefaultKickUser(getViewDecor().context, intent.getStringExtra("content")!!)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        saveActive()
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, IntentFilter(
            Configs.NoticeKickUser))

        if (getSharePreSystem(this, Configs.NoticeKickUser, false)) { //check is kick user
            val handler = Handler(Looper.getMainLooper())
            handler.post { showDialogDefaultKickUser(getViewDecor().context, getString(R.string.alert_user_kick)) }
        }
    }

    override fun onPause() {
        super.onPause()
        saveUnActive()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver)
    }

    ///////////////////////////////////// BroadcastReceiver //

    ///////////////////////////////////////////////////////// Function App Memo //////////////////////////////////////////////////////

}