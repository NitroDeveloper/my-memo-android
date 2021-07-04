package com.nitroex.my_memo.utils.webview

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.nitroex.my_memo.BaseActivity

class WebViewManager : BaseActivity()  {

    @SuppressLint("SetJavaScriptEnabled")
    fun showMemoDetail(webView: WebView, strMemoDetail: String){
        webView.webChromeClient = WebChromeClient()
        webView.settings.apply {
            javaScriptEnabled = true
        }
        webView.loadDataWithBaseURL(null, strMemoDetail, "text/html", "UTF-8", null)
    }

}