package com.github.salhe.cash_gift

import com.github.salhe.cash_gift.utils.HanziOrPinyin
import com.github.salhe.cash_gift.utils.similarWith
import org.junit.Assert.assertFalse
import org.junit.Test


class PinyinTest {

    @Test
    fun fetchPinyinTest() {
        assert(HanziOrPinyin.Pinyin("chang").pinyins.containsAll(listOf("chang")))
        assert(HanziOrPinyin.Hanzi('左').pinyins.containsAll(listOf("zuo")))
        assert(HanziOrPinyin.Hanzi('长').pinyins.containsAll(listOf("chang", "zhang")))
    }

    @Test
    fun simplePinyinEqualsTest() {
        assert(HanziOrPinyin.Hanzi('长') == HanziOrPinyin.Hanzi('涨'))
        assert(HanziOrPinyin.Hanzi('长') == HanziOrPinyin.Hanzi('常'))
        assert(HanziOrPinyin.Hanzi('长') as HanziOrPinyin == HanziOrPinyin.Pinyin("zhang") as HanziOrPinyin)
    }

    @Test
    fun hanziFullEqualsByPinyinTest() {
        assert("何长春" similarWith "何涨春")
        assert("何长春" similarWith "何常春")
        assert("何短春" similarWith "何断春")
        assert("武汉小学" similarWith "武汗小雪")
        assert("武汉小学" similarWith "捂汗小雪")

        assert("何长春" similarWith "何chang春")
        assert("何长春" similarWith "何zhang春")
        assert("何长春" similarWith "何zhang chun")

        assertFalse("何长春" similarWith "何短春")
        assertFalse("何长春" similarWith "何duan春")
    }

    @Test
    fun hanziPartialEqualsByPinyinTest() {
        assert("何长春" similarWith "chang春")
        assert("何长春" similarWith "zhang春")
        assert("何长春" similarWith "zhang chun")
        assertFalse("何长春" similarWith "duan chun")
    }

}