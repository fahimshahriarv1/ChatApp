package com.example.chatappstarting.presentation.ui.splashScreen

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chatappstarting.R
import com.example.chatappstarting.presentation.navgraph.NavGraph
import com.example.chatappstarting.presentation.ui.base.BaseActivity
import com.example.chatappstarting.presentation.ui.splashScreen.ui.SplashScreen
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity() {
    val viewModel by viewModels<SplashViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideToolbar()
        hideSystemUI()
        setStatusBarColor(R.color.app_main)
        setContent {
            SplashScreen()
            if (viewModel.startDest.isNotEmpty())
                NavGraph(startDest = viewModel.startDest)
        }
    }
}