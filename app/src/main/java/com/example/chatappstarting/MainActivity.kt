package com.example.chatappstarting

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.chatappstarting.presentation.navgraph.NavGraph
import com.example.chatappstarting.presentation.navgraph.Route

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            NavGraph(startDest = Route.LoginScreen.route)
        }
    }
}