package com.fahimshahriarv1.mtom.presentation.ui.splashScreen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.fahimshahriarv1.mtom.R
import com.fahimshahriarv1.mtom.presentation.navgraph.NavGraph
import com.fahimshahriarv1.mtom.presentation.navgraph.Route
import com.fahimshahriarv1.mtom.presentation.ui.base.BaseActivity
import com.fahimshahriarv1.mtom.presentation.ui.home.HomeActivity
import com.fahimshahriarv1.mtom.presentation.ui.splashScreen.ui.SplashScreen
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity() {
    private val viewModel by viewModels<SplashViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideToolbar()
        hideSystemUI()
        setStatusBarColor(R.color.app_main)
        setContent {
            when (viewModel.startDest.value) {
                Route.AppMain.route -> gotoHome()
                Route.AppAuth.route -> NavGraph(startDest = Route.AppAuth.route)
                else -> SplashScreen()
            }
        }
    }

    private fun gotoHome() {
        val i = Intent(this, HomeActivity::class.java)
        startActivity(i)
    }
}