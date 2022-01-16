package com.github.salhe.cash_gift.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.salhe.cash_gift.ui.theme.CashGiftLeeTheme
import com.github.salhe.cash_gift.utils.CashGift
import com.github.salhe.cash_gift.utils.similarWith
import com.github.salhe.cash_gift.utils.toCurrencyString


@Composable
fun MainContent(cashes: List<CashGift>) {
    CashGiftLeeTheme {
        Surface(
            color = MaterialTheme.colors.background,
            modifier = Modifier.padding(2.dp)
        ) {
            var nameQuery by remember { mutableStateOf("") }
            var similaritySearch by remember { mutableStateOf(true) }

            // 筛选需要查看的送礼
            val cashesToShow = when {
                nameQuery.isEmpty() -> cashes
                similaritySearch -> cashes.filter { it.name.contains(nameQuery) || it.name similarWith nameQuery }
                !similaritySearch -> cashes.filter { it.name.contains(nameQuery) }

                // impossible
                else -> error("Unhandled filter condition!")
            }

            Column {
                // 筛选相关
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 3.dp)
                ) {
                    Text(text = "姓名：")
                    TextField(
                        value = nameQuery,
                        onValueChange = { nameQuery = it },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    val interactionSource =
                        remember { MutableInteractionSource() } // 关联CheckBox和Text的交互过程
                    Checkbox(
                        checked = similaritySearch,
                        onCheckedChange = { similaritySearch = it },
                        interactionSource = interactionSource
                    )
                    Text(
                        text = "模糊搜索",
                        Modifier.clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) { similaritySearch = !similaritySearch })
                }

                // 账本列表
                Box(modifier = Modifier.weight(1f)) {
                    CashGiftsList(cashesToShow)
                }

                Row {
                    Box(modifier = Modifier.weight(1f))
                    Text(
                        text = "合计：${
                            cashesToShow.sumOf(CashGift::cash).toCurrencyString()
                        }"
                    )
                }
            }
        }
    }
}

@Composable
private fun Item(content: @Composable RowScope.() -> Unit) {
    Card(
        modifier = Modifier
            .padding(10.dp, 5.dp)
    ) {
        Row(modifier = Modifier
            .clickable { }
            .padding(10.dp, 5.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            content()
        }
    }
}

@Composable
private fun CashGiftsList(cashesToShow: List<CashGift>) {
    LazyColumn {
        items(cashesToShow) {
            CashGiftItem(it)
        }
        item {
            Item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "没有了~~")
                }
            }
        }
    }
}

@Composable
private fun CashGiftItem(it: CashGift) {
    Item {
        Text(text = it.name, fontSize = 24.sp)
        Spacer(modifier = Modifier.width(5.dp))

        if (it.location.isNotBlank())
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = "地点",
            )
        Text(text = it.location)
        Spacer(modifier = Modifier.width(2.dp))

        if (it.remark.isNotBlank())
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = "备注",
                modifier = Modifier.scale(0.5f)
            )
        Text(
            text = it.remark,
            fontStyle = FontStyle.Italic,
            color = Color.Gray
        )

        Box(modifier = Modifier.weight(1f))

        Text(text = it.cash.toCurrencyString())
    }
}
