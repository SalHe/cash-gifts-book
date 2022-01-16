package com.github.salhe.cash_gift.utils

import android.content.Context

data class CashGift(
    val name: String,
    val cash: UInt,
    val location: String,
    val remark: String
)

private var cashGifts: List<CashGift>? = null

internal fun Context.loadCashes() =
    if (cashGifts != null) cashGifts!! else
        resources.assets.open("cash.csv")
            .readBytes()
            .let { String(it) }
            .lines()
            .filter { it.isNotEmpty() }
            .map { it.split(",") }
            .filter { it.size == 4 }
            .map {
                CashGift(
                    name = it[0],
                    cash = it[1].toUInt(),
                    location = it[2],
                    remark = it[3]
                )
            }
            .also { cashGifts = it }
