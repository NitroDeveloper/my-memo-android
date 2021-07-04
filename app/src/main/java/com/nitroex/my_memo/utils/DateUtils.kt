package com.nitroex.my_memo.utils

import android.app.DatePickerDialog
import android.content.Context
import com.nitroex.my_memo.BaseActivity
import java.text.SimpleDateFormat
import java.util.*

class DateUtils : BaseActivity(){

    private lateinit var context: Context
    private var instance: DateUtils? = null
    var isSelectLang = ""

    private var formatApi = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private var formatShowEN = SimpleDateFormat("d MMMM yyyy", Locale.US)
    private var formatShowTH = SimpleDateFormat("d MMMM yyyy", Locale("th", "TH"))

    private val digitTH = arrayOf("๐", "๑", "๒", "๓", "๔", "๕", "๖", "๗", "๘", "๙")
    private val digitEN = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")

    fun newInstance(): DateUtils {
        if (instance == null) instance = DateUtils()
        return instance as DateUtils
    }
    fun setEasyDate(context: Context) { this.context = context }

    // เรียก Date Picker Dialog ที่ถูกเรียกใช้งาน
    fun getDateDialog(cld: Calendar): DatePickerDialog{
        val year = cld.get(Calendar.YEAR)
        val month = cld.get(Calendar.MONTH)
        val day = cld.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(context)
        dpd.updateDate(year, month, day)

        return dpd
    }

    // เรียกวันที่ปัจจุบันตามแบบ show หรือแบบ api
    fun getDateNow(isShow: Boolean, isBaseLang: Boolean): String {
        return if (isShow) {
            if (isBaseLang) { getDateFormatShowByBaseLang(context, Date())
            }else{ getDateFormatShow(context, Date()) }
        }else{ getDateFormatApi(Date()) }
    }

    private fun convertYearEnToTh(strDate: String): String {
        val lastIndex = strDate.length
        val firstIndex = lastIndex-4
        val strYear = strDate.substring(firstIndex, lastIndex)
        return strDate.replace(strYear, (strYear.toInt()+543).toString())
    }

    // เรียกวันที่ที่ถูกเลือกอยู่ในปฏิทิน
    private fun getSelectDate(dateUtils: DateUtils): Date {
        return dateUtils.getCalendarNow().time
    }
    fun getCalendarNow(): Calendar {
        return Calendar.getInstance()
    }
    fun getCalendarApi(str: String): Calendar {
        val calendar = Calendar.getInstance()
        calendar.time = formatApi.parse(str) as Date
        return  calendar
    }

    // เแปลงค่าเมื่อเลือกวันที่แบบเรียลไทม์
    fun convertDateByPickerDialog(year: Int, month: Int, day: Int): Date{
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        return calendar.time
    }

    // แปลงวันที่จากฟอร์แมตสตริงเป็นฟอร์แมตแบบเต็มเพื่อโชว์
    fun convertDateApiToShow(str: String, isBaseLang: Boolean): String {
        return if (isBaseLang) { getDateFormatShowByBaseLang(context, formatApi.parse(str) as Date)
        }else{ getDateFormatShow(context, formatApi.parse(str) as Date) }
    }
    fun convertDateApiByLang(dateApi: String, lang: String): String{
        val date = formatApi.parse(dateApi) as Date
        return if (lang == Configs.English) {
         //   formatShowEN.format(date)
            convertDateToFormatEN(convertYearEnToTh(formatShowTH.format(date)))
        } else {
            convertDateToFormatThai(convertYearEnToTh(formatShowTH.format(date)))
        }
    }
    /////////////////////////////////////////////////// getDateFormat
    fun getDateFormatShowByBaseLang(context: Context, date: Date): String {
        return if ((context as BaseActivity).getLanguage() == Configs.English) {
            formatShowEN.format(date)
        } else {
            convertYearEnToTh(formatShowTH.format(date))
        }
    }

    private fun getDateFormatShow(context: Context, date: Date): String {
        return if (isSelectLang == Configs.English) {
            convertDateToFormatEN(convertYearEnToTh(formatShowTH.format(date)))
        } else {
            convertDateToFormatThai(convertYearEnToTh(formatShowTH.format(date)))
        }
    }

    fun getDateFormatShow(date: Date): String {
        val sdf: SimpleDateFormat
        val strDate: String
        if (isSelectLang == Configs.English) {
            sdf = formatShowTH
            strDate = convertDateToFormatEN(convertYearEnToTh(sdf.format(date)))
        } else {
            sdf = formatShowTH
            strDate = convertDateToFormatThai(convertYearEnToTh(sdf.format(date)))
        }
        return strDate
    }

    fun getDateFormatShow(dateUtils: DateUtils): String {
        val sdf = if (isSelectLang == Configs.English) { formatShowEN } else { formatShowTH }
        return sdf.format(getSelectDate(dateUtils))
    }

    fun getDateFormatApi(date: Date): String {
        return formatApi.format(date)
    }
    fun getDateFormatApi(dateUtils: DateUtils): String {
        return formatApi.format(getSelectDate(dateUtils))
    }

    private fun convertDateToFormatThai(date: String): String {
     val textTH = getThaiFormatShow(date.trim { it <= ' ' })
        return textTH.format(date)
    }

    private fun getThaiFormatShow(date: String): String {
        if (date.isEmpty()) return ""
            val sb = java.lang.StringBuilder()
            for (c in date.toCharArray()) {
                if (Character.isDigit(c)) {
                    val index = digitTH[Character.getNumericValue(c)]
                    sb.append(index)
                } else {
                    sb.append(c)
                }

            }
            return sb.toString()
        }

    private fun convertDateToFormatEN(date: String): String {
        val textTH = getENFormatShow(date.trim { it <= ' ' })
        return textTH.format(date)
    }

    private fun getENFormatShow(date: String): String {
        if (date.isEmpty()) return ""
        val sb = java.lang.StringBuilder()
        for (c in date.toCharArray()) {
            if (Character.isDigit(c)) {
                val index = digitEN[Character.getNumericValue(c)]
                sb.append(index)
            } else {
                sb.append(c)
            }

        }
        return sb.toString()
    }

   fun  covertDocNoToFormatThai(date: String, lang: String): String {
       val textTH = getThaiFormatDocNoLang(date.trim { it <= ' '}, lang)
       return textTH.format(date)
   }

    private fun getThaiFormatDocNoLang(date: String, lang: String): String {
        if (date.isEmpty()) return ""
        val sb = java.lang.StringBuilder()
            for (c in date.toCharArray()){
                if (Character.isDigit(c)){
                    if (lang == Configs.English){
                        val index = digitEN[Character.getNumericValue(c)]
                         sb.append(index)
                    } else {
                        val index = digitTH[Character.getNumericValue(c)]
                        sb.append(index)
                    }
                  }  else{
                    sb.append(c)
                }

            }

        return sb.toString()

    }
    /////////////////////////////////////////////////// getDateFormat
}