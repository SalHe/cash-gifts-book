package com.github.salhe.cash_gift

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.github.salhe.cash_gift.ui.MainContent
import com.github.salhe.cash_gift.utils.loadCashes

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainContent(applicationContext.loadCashes())
        }
    }


}
