package com.example.chatappstarting.ui.splashScreen

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.example.chatappstarting.ui.base.BaseActivity
import com.example.chatappstarting.ui.login.LoginActivity
import com.example.chatappstarting.ui.splashScreen.ui.SplashScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideToolbar()
        hideSystemUI()
        setContent {
                SplashScreen()
        }

        lifecycleScope.launch {
            delay(3000)
            startActivity(LoginActivity::class.java)
            finish()
        }
    }
}