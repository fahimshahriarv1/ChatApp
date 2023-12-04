package com.example.chatappstarting.ui.splashScreen

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.example.chatappstarting.R
import com.example.chatappstarting.domain.usecases.data.LocalUserLogin
import com.example.chatappstarting.ui.base.BaseActivity
import com.example.chatappstarting.ui.home.HomeActivity
import com.example.chatappstarting.ui.login.LoginActivity
import com.example.chatappstarting.ui.splashScreen.ui.SplashScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity() {
    @Inject
    lateinit var localUserLogin: LocalUserLogin
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideToolbar()
        hideSystemUI()
        setStatusBarColor(R.color.app_main)
        setContent {
            SplashScreen()
        }

        lifecycleScope.launch {
            localUserLogin.getUSerLoggedInState.getLoggedInState().collect {
                delay(3000)
                if (it)
                    startActivity(LoginActivity::class.java)
                else
                    startActivity(HomeActivity::class.java)
                finish()
            }
        }
    }
}