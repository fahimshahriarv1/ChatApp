package com.example.chatappstarting.ui.home

import android.os.Bundle
import androidx.activity.compose.setContent
import com.example.chatappstarting.ui.base.BaseActivity
import com.example.chatappstarting.ui.home.views.HomeScreen

class HomeActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideToolbar()
        hideSystemUI()
        setContent {
            HomeScreen()
        }
    }
}