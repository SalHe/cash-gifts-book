package com.github.salhe.cash_gift.utils

import java.text.NumberFormat
import java.util.*

var currencyFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale.CHINA)

fun UInt.toChineseNumber(): String {
    var src = this.toInt()
    val num = arrayOf("零", "一", "二", "三", "四", "五", "六", "七", "八", "九")
    val unit = arrayOf("", "十", "百", "千", "万", "十", "百", "千", "亿", "十", "百", "千")
    var dst = ""
    var count = 0
    while (src > 0) {
        dst = num[src % 10] + unit[count] + dst
        src /= 10
        count++
    }
    return dst.replace("零[千百十]".toRegex(), "零").replace("零+万".toRegex(), "万")
        .replace("零+亿".toRegex(), "亿").replace("亿万".toRegex(), "亿零")
        .replace("零+".toRegex(), "零").replace("零$".toRegex(), "")
}

fun UInt.toCurrencyString() = "${currencyFormat.format(this.toLong())} (${this.toChineseNumber()}元)"