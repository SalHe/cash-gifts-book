package com.github.salhe.cash_gift.utils

import net.sourceforge.pinyin4j.PinyinHelper
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType

private val pinyinOutputFormat = HanyuPinyinOutputFormat().apply {
    toneType = HanyuPinyinToneType.WITHOUT_TONE
}

internal abstract class HanziOrPinyin {

    abstract fun getPinyinArray(): List<String>

    override operator fun equals(other: Any?): Boolean {
        if (other !is HanziOrPinyin) return false
        return super.equals(other)
                || (this.getPinyinArray() intersect other.getPinyinArray().toSet()).isNotEmpty()
    }

    override fun hashCode(): Int {
        return getPinyinArray().joinToString().hashCode()
    }

    class Hanzi(private val hanzi: Char) : HanziOrPinyin() {

        var pinyins = PinyinHelper.toHanyuPinyinStringArray(hanzi, pinyinOutputFormat).toList()

        override fun getPinyinArray() = pinyins

        override fun toString(): String = hanzi.toString()

    }

    class Pinyin(private val pinyin: String) : HanziOrPinyin() {

        val pinyins = arrayOf(pinyin).toList()

        override fun getPinyinArray() = pinyins.toList()

        override fun toString(): String = pinyin
    }

}

object Pinyin {

    private fun String.splitByHanziOrPinyin(): List<HanziOrPinyin> {
        val words = mutableListOf<HanziOrPinyin>()
        var p = 0
        while (p < this.length) {
            var ch = this[p]
            when {
                ch.isHanzi() -> words.add(HanziOrPinyin.Hanzi(ch))
                ch.isLetter() -> words.add(HanziOrPinyin.Pinyin(
                    buildString {
                        while (ch.isLetterFixed()) {
                            append(ch)
                            p++
                            if (p < this@splitByHanziOrPinyin.length)
                                ch = this@splitByHanziOrPinyin[p]
                            else {
                                p++
                                break
                            }
                        }
                        p--
                    }
                ))
                else -> {}
            }
            p++
        }
        return words
    }

    /**
     * 单纯用[Char.isLetter]判断的话，针对汉字我测试结果是不对的。
     */
    private fun Char.isLetterFixed() =
        this.isLetter() && (this.isUpperCase() || this.isLowerCase())

    private fun Char.isHanzi() = this >= 0x4E00.toChar() && this <= 0x9FBB.toChar()

    fun isStringSimilar(first: String, second: String): Boolean {
        val firstWords = first.splitByHanziOrPinyin()
        val secondWords = second.splitByHanziOrPinyin()
        if (firstWords.size != secondWords.size) {
            val (w1, w2) =
                if (firstWords.size > secondWords.size)
                    Pair(firstWords, secondWords) else Pair(secondWords, firstWords)
            val minSize = w2.size
            for (i in 0..(w1.size - w2.size)) {
                if (isWordSimilar(w1.subList(i, i + minSize), w2))
                    return true
            }
            return false
        } else {
            return isWordSimilar(firstWords, secondWords)
        }
    }

    private fun isWordSimilar(
        firstWords: List<HanziOrPinyin>,
        secondWords: List<HanziOrPinyin>
    ): Boolean {
        if (firstWords.size != secondWords.size) return false
        for ((a, b) in firstWords.zip(secondWords)) {
            if (a != b) {
                return false
            }
        }
        return true
    }

}

infix fun String.similarWith(other: String) = Pinyin.isStringSimilar(this, other)