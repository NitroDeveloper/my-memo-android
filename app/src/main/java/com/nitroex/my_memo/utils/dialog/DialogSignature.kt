package com.nitroex.my_memo.utils.dialog

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.nitroex.my_memo.BaseActivity
import com.nitroex.memo.my_memo.R
import com.nitroex.my_memo.utils.Configs
import com.nitroex.my_memo.utils.listener.OnDialogCallbackListener
import com.github.gcacace.signaturepad.views.SignaturePad
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class DialogSignature : DialogFragment() {
    private var btnBack: ImageView? = null
    private var btnClear: ImageView? = null
    private var mSignaturePad: SignaturePad? = null
    private var rela_skip: RelativeLayout? = null
    private var rela_save: RelativeLayout? = null
    private var rela_my: RelativeLayout? = null
    private var imageViewSignature: ImageView? = null
    private var appbar_title: TextView? = null
    private var viewImage: ConstraintLayout? = null
    private var listener: OnDialogCallbackListener? = null
    var mOnInputListener: OnInputListener? = null
    private var isFromPreview = false
    private var tvSave : TextView? = null

    interface OnInputListener { fun sendPathPicSignature(isSkip: Boolean, input: String?) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogFullScreen)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val view: View = inflater.inflate(R.layout.dialog_signature_update, container, false)

        btnBack = view.findViewById<View>(R.id.btnBack) as ImageView
        btnClear = view.findViewById<View>(R.id.btnClear) as ImageView
        mSignaturePad = view.findViewById<View>(R.id.signature_pad) as SignaturePad
        rela_save = view.findViewById(R.id.rela_save)
        rela_skip = view.findViewById(R.id.rela_skip)
        viewImage = view.findViewById(R.id.viewImage)
        imageViewSignature = view.findViewById(R.id.imageViewSignature)
        appbar_title = view.findViewById(R.id.tvAppbarTitle)
        rela_my = view.findViewById(R.id.rela_my)
        tvSave = view.findViewById(R.id.textViewSave)

        val signature = (context as BaseActivity).getSharePreUser(context as BaseActivity, Configs.EmpSignature, "")
        if (signature.isEmpty() || signature == "null") { rela_my!!.visibility = View.GONE
        } else { rela_my!!.visibility = View.VISIBLE }

        if (isFromPreview) {
            appbar_title!!.text = context!!.getString(R.string.signature_name)
            rela_skip!!.visibility = View.VISIBLE
            tvSave!!.text = context!!.getString(R.string.confirm)
        }else{
            appbar_title!!.text = context!!.getString(R.string.signature_name_update)
        }

        rela_my!!.setOnClickListener {
            (context as BaseActivity).setSignatureByBase64(signature, mSignaturePad!!)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnClear!!.setOnClickListener {
            mSignaturePad!!.clear()
            viewImage!!.visibility = View.GONE
            mSignaturePad!!.visibility = View.VISIBLE
        }
        btnBack!!.setOnClickListener { dismiss() }
        rela_save!!.setOnClickListener {
            addJpgSignatureToGallery()
        }
        rela_skip!!.setOnClickListener {
            mOnInputListener!!.sendPathPicSignature(true,"")
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mOnInputListener = activity as OnInputListener?
            listener = activity as OnDialogCallbackListener?
        } catch (e: ClassCastException) {
            Log.e(TAG, "onAttach: ClassCastException: " + e.message)
        }
    }

    private fun addJpgSignatureToGallery(): Boolean {
         try {
             val file = File(context!!.cacheDir.toString(), Configs.EasyImageCache)
             val photo = File(file.path + File.separator + String.format("Signature_%d.png", System.currentTimeMillis()))
             if (!file.exists()) { if (!file.mkdirs()) {} }

            saveBitmapToPNG(photo)

             if (!mSignaturePad!!.isEmpty){
                 mOnInputListener!!.sendPathPicSignature(false, photo.path)
             }else{
                 mOnInputListener!!.sendPathPicSignature(false, "")
             }

             return true
        } catch (e: IOException) {
            e.printStackTrace()
             (context as BaseActivity).showDialogAlert(false,"Error: Not Create Signature")
             return false
        }
    }

    @Throws(IOException::class)
    fun saveBitmapToPNG(photo: File?) {
        val newBitmap = mSignaturePad!!.transparentSignatureBitmap
        val canvas = Canvas(newBitmap)
        canvas.drawBitmap(newBitmap, 0f, 0f, null)

        val stream: OutputStream = FileOutputStream(photo)
        newBitmap.compress(Bitmap.CompressFormat.PNG, 0, stream)
        stream.close()
    }

    companion object {
        const val TAG = "signature"
        var dialogSignature: DialogSignature? = null
        fun display(fragmentManager: FragmentManager?, listener: OnDialogCallbackListener, isFromPreview: Boolean): DialogSignature? {
            dialogSignature = DialogSignature()
            if (fragmentManager != null) {
                dialogSignature!!.show(fragmentManager, TAG)
            }
            dialogSignature!!.listener = listener
            dialogSignature!!.isFromPreview = isFromPreview
            return dialogSignature
        }
    }
}