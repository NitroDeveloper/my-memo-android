package com.nitroex.my_memo.utils

import android.app.Activity
import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.nitroex.my_memo.BaseActivity
import com.nitroex.memo.my_memo.R
import com.nitroex.my_memo.utils.listener.OnLangChangeListener
import kotlinx.android.synthetic.main.activity_internal_form.*
import kotlinx.android.synthetic.main.activity_internal_form.blockRbCNone
import kotlinx.android.synthetic.main.activity_internal_form.blockRbConfiden
import kotlinx.android.synthetic.main.activity_internal_form.blockRbEN
import kotlinx.android.synthetic.main.activity_internal_form.blockRbImmediate
import kotlinx.android.synthetic.main.activity_internal_form.blockRbPriority
import kotlinx.android.synthetic.main.activity_internal_form.blockRbSNone
import kotlinx.android.synthetic.main.activity_internal_form.blockRbSecret
import kotlinx.android.synthetic.main.activity_internal_form.blockRbTH
import kotlinx.android.synthetic.main.activity_internal_form.blockRbTopSecret
import kotlinx.android.synthetic.main.activity_internal_form.blockRbUrgent
import kotlinx.android.synthetic.main.activity_internal_form.rbCNone
import kotlinx.android.synthetic.main.activity_internal_form.rbConfiden
import kotlinx.android.synthetic.main.activity_internal_form.rbEN
import kotlinx.android.synthetic.main.activity_internal_form.rbImmediate
import kotlinx.android.synthetic.main.activity_internal_form.rbPriority
import kotlinx.android.synthetic.main.activity_internal_form.rbSNone
import kotlinx.android.synthetic.main.activity_internal_form.rbSecret
import kotlinx.android.synthetic.main.activity_internal_form.rbTH
import kotlinx.android.synthetic.main.activity_internal_form.rbTopSecret
import kotlinx.android.synthetic.main.activity_internal_form.rbUrgent
import kotlinx.android.synthetic.main.activity_internal_form.tvCNone
import kotlinx.android.synthetic.main.activity_internal_form.tvConfiden
import kotlinx.android.synthetic.main.activity_internal_form.tvEN
import kotlinx.android.synthetic.main.activity_internal_form.tvImmediate
import kotlinx.android.synthetic.main.activity_internal_form.tvPriority
import kotlinx.android.synthetic.main.activity_internal_form.tvSNone
import kotlinx.android.synthetic.main.activity_internal_form.tvSecret
import kotlinx.android.synthetic.main.activity_internal_form.tvTH
import kotlinx.android.synthetic.main.activity_internal_form.tvTopSecret
import kotlinx.android.synthetic.main.activity_internal_form.tvUrgent

class RadioBtnUtils: BaseActivity(){
    private lateinit var ctx: Context
    private lateinit var act: Activity
    private var instance: RadioBtnUtils? = null
    private lateinit var listener : OnLangChangeListener

    var isSelectConfident = 0
    var isSelectSpeedLevel = 0
    var isSelectShowTo = 0
    var isSelectShowFrom = 0
    var isSelectLang = ""
    var isConfidentName = ""
    var isSpeedLevelName = ""
    var defShowForm = ""

    ///////////////////////////////////////////////////////// Setting Custom Radio Button /////////////////////////////////////////////////////////

    fun newInstance(): RadioBtnUtils {
        if (instance == null) instance = RadioBtnUtils()
        return instance as RadioBtnUtils
    }

    fun setView(context: Context) {
        this.ctx = context
        this.act = context as Activity
    }

    fun setViewConfidential(){
        act.blockRbCNone.setOnClickListener { setRadioConfident(0) }
        act.blockRbConfiden.setOnClickListener { setRadioConfident(1) }
        act.blockRbSecret.setOnClickListener { setRadioConfident(2) }
        act.blockRbTopSecret.setOnClickListener { setRadioConfident(3) }
    }

    fun setViewSpeedLevel(){
        act.blockRbSNone.setOnClickListener { setRadioSpeedLevel(0) }
        act.blockRbPriority.setOnClickListener { setRadioSpeedLevel(1) }
        act.blockRbImmediate.setOnClickListener { setRadioSpeedLevel(2) }
        act.blockRbUrgent.setOnClickListener { setRadioSpeedLevel(3) }
    }

    fun setViewShowTo(){
        act.rbShowTo.setOnClickListener {setRadioShowTo(0) }
        act.rbShowNoTo.setOnClickListener { setRadioShowTo(1) }
    }

    fun setViewShowFrom(strDefShowForm: String) {
        defShowForm = strDefShowForm
        act.rbShowFrom.setOnClickListener {setRadioShowFrom(0) }
        act.rbShowNoFrom.setOnClickListener { setRadioShowFrom(1) }
    }

    fun setRadioConfident(index: Int) {
        setStatusRadioView(false, act.rbCNone, act.tvCNone)
        setStatusRadioView(false, act.rbConfiden, act.tvConfiden)
        setStatusRadioView(false, act.rbSecret, act.tvSecret)
        setStatusRadioView(false, act.rbTopSecret, act.tvTopSecret)
        when (index) {
            0 -> { setStatusRadioView(true, act.rbCNone, act.tvCNone); isConfidentName = act.tvCNone.text.toString() }
            1 -> { setStatusRadioView(true, act.rbConfiden, act.tvConfiden); isConfidentName = act.tvConfiden.text.toString()  }
            2 -> { setStatusRadioView(true, act.rbSecret, act.tvSecret); isConfidentName = act.tvSecret.text.toString()  }
            3 -> { setStatusRadioView(true, act.rbTopSecret, act.tvTopSecret); isConfidentName = act.tvTopSecret.text.toString()  }
        }
        isSelectConfident = index
    }

    fun setRadioSpeedLevel(index: Int) {
        setStatusRadioView(false, act.rbSNone, act.tvSNone)
        setStatusRadioView(false, act.rbPriority, act.tvPriority)
        setStatusRadioView(false, act.rbImmediate, act.tvImmediate)
        setStatusRadioView(false, act.rbUrgent, act.tvUrgent)
        when (index) {
            0 -> { setStatusRadioView(true, act.rbSNone, act.tvSNone); isSpeedLevelName = act.tvSNone.text.toString() }
            1 -> { setStatusRadioView(true, act.rbPriority, act.tvPriority); isSpeedLevelName = act.tvPriority.text.toString() }
            2 -> { setStatusRadioView(true, act.rbImmediate, act.tvImmediate); isSpeedLevelName = act.tvImmediate.text.toString() }
            3 -> { setStatusRadioView(true, act.rbUrgent, act.tvUrgent); isSpeedLevelName = act.tvUrgent.text.toString() }
        }
        isSelectSpeedLevel = index
    }

    fun setRadioShowTo(index: Int) {
        when (index) {
            0 -> {
                setViewVisible(act.rbShowNoTo,true)
                setViewVisible(act.rbShowTo,false)
                act.edtShowTo.isEnabled = false
                act.edtShowTo.hint = ""
                act.edtShowTo.setText("")
            }
            1 -> {
                setViewVisible(act.rbShowNoTo,false)
                setViewVisible(act.rbShowTo,true)
                act.edtShowTo.isEnabled = true
                act.edtShowTo.hint =  act.getString(R.string.please_input_show_to)
            }
        }
        isSelectShowTo = index
    }

    fun setDisplayFormLangChange(listener: OnLangChangeListener) {
        this.listener = listener
        act.blockRbEN.setOnClickListener {setRadioShowLanguage(0)}
        act.blockRbTH.setOnClickListener {setRadioShowLanguage(1)}

    }

    fun setRadioShowLanguage(index: Int){
        var language = ""
        when (index){
            0 -> {
                setStatusRadioView(false, act.rbTH, act.tvTH)
                setStatusRadioView(true, act.rbEN, act.tvEN)
                language = "en"
            }
            1 ->{
                setStatusRadioView(true, act.rbTH, act.tvTH)
                setStatusRadioView(false, act.rbEN, act.tvEN)
                language = "th"
            }
        }
        isSelectLang = language
        try { listener.onChange(language)
        } catch (e: Exception) { }
    }

    fun setRadioShowFrom(index: Int) {
        when (index) {
            0 -> {
                setViewVisible(act.rbShowNoFrom,true)
                setViewVisible(act.rbShowFrom,false)
                act.edtFromName.apply {
                    isEnabled = false
                    hint = ""
                    setText("")
                }
            }
            1 -> {
                setViewVisible(act.rbShowNoFrom,false)
                setViewVisible(act.rbShowFrom,true)
                act.edtFromName.apply {
                    isEnabled = true
                    hint = act.getString(R.string.please_input_show_from)
                    setText(defShowForm)
                }
            }
        }
        isSelectShowFrom = index
    }

    private fun setStatusRadioView(isCheck: Boolean, radio: ImageView, text: TextView){
        if (isCheck) {
            radio.setImageResource(R.drawable.radioselected)
            text.setTextColor(ContextCompat.getColor(ctx, R.color.txtGrayDark))
        }else{
            radio.setImageResource(R.drawable.radio)
            text.setTextColor(ContextCompat.getColor(ctx, R.color.txtGrayLight))
        }
    }

    ///////////////////////////////////////////////////////// Setting Custom Radio Button /////////////////////////////////////////////////////////

}