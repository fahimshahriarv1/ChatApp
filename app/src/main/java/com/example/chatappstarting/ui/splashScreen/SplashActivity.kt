package com.example.chatappstarting.ui.splashScreen

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chatappstarting.R
import com.example.chatappstarting.ui.base.BaseActivity
import com.example.chatappstarting.ui.home.HomeActivity
import com.example.chatappstarting.ui.login.LoginActivity
import com.example.chatappstarting.ui.splashScreen.ui.SplashScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideToolbar()
        hideSystemUI()
        setStatusBarColor(R.color.app_main)
        setContent {
            val vm: SplashViewModel = hiltViewModel()
            SplashScreen()

            LaunchedEffect(key1 = true, block = {
                vm.saveLoginState()
                vm.getLoggedInState()
                vm.isUserLoggedIn.collect {
                    delay(3000)
                    if (it)
                        startActivity(LoginActivity::class.java)
                    else
                        startActivity(HomeActivity::class.java)

                    finish()
                }
            })
        }
    }
}