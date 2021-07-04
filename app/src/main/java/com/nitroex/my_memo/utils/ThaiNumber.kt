package com.nitroex.my_memo.utils

class ThaiNumber {

    private var valueText: String? = null

    // ···········Methods··············
    fun getText(amount: Double): String? {
        valueText = getThaiNumber(amount.toString())
        return valueText
    }

    fun getText(amount: Float): String? {
        valueText = getThaiNumber(amount.toString())
        return valueText
    }

    fun getText(amount: Int): String? {
        valueText = getThaiNumber(amount.toString())
        return valueText
    }

    fun getText(amount: Long): String? {
        valueText = getThaiNumber(amount.toString())
        return valueText
    }

    fun getText(amount: String): String? {
        valueText = getThaiNumber(amount.trim { it <= ' ' })
        return valueText
    }

    fun getText(amount: Number): String? {
        valueText = getThaiNumber(amount.toString())
        return valueText
    }

    companion object {
        private val DIGIT_TH = arrayOf("๐", "๑", "๒", "๓", "๔", "๕", "๖", "๗", "๘", "๙")
        
        private fun getThaiNumber(amount: String?): String {
            if (amount == null || amount.isEmpty()) return ""
            val sb = java.lang.StringBuilder()
            for (c in amount.toCharArray()) {
                if (Character.isDigit(c)) {
                    val index = DIGIT_TH[Character.getNumericValue(c)]
                    sb.append(index)
                } else {
                    sb.append(c)
                }
            }
            return sb.toString()
        }
    }

    }