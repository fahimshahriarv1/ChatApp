package com.example.chatappstarting.presentation.ui.home

import android.os.Bundle
import androidx.activity.compose.setContent
import com.example.chatappstarting.presentation.navgraph.NavGraph
import com.example.chatappstarting.presentation.navgraph.Route
import com.example.chatappstarting.presentation.ui.base.BaseActivity

class HomeActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideToolbar()
        hideSystemUI()
        setContent {
            NavGraph(startDest = Route.HomeScreen.route)
        }
    }
}