package com.example.chatappstarting.presentation.ui.home

import android.os.Bundle
import androidx.activity.compose.setContent
import com.example.chatappstarting.presentation.ui.base.BaseActivity
import com.example.chatappstarting.presentation.ui.home.views.HomeScreen

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