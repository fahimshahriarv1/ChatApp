package com.fahimshahriarv1.mtom.presentation.ui.home

import android.os.Bundle
import androidx.activity.compose.setContent
import com.fahimshahriarv1.mtom.presentation.navgraph.NavGraph
import com.fahimshahriarv1.mtom.presentation.navgraph.Route
import com.fahimshahriarv1.mtom.presentation.ui.base.BaseActivity

class HomeActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideToolbar()
        hideSystemUI()
        setContent {
            NavGraph(startDest = Route.AppMain.route)
        }
    }
}