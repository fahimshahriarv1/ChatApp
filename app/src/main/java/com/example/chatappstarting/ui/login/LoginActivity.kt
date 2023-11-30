package com.example.chatappstarting.ui.login

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import com.example.chatappstarting.ui.base.BaseActivity
import com.example.chatappstarting.ui.login.ui.LoginScreen

class LoginActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideToolbar()
        hideSystemUI()
        val uname = mutableStateOf("")
        val pass = mutableStateOf("")
        val isError = mutableStateOf(false)
        setContent {
            LoginScreen(
                uname,
                pass,
                isError,
                unameChanged = {
                    uname.value = it
                },
                passChanged = {
                    pass.value = it
                },
                onLoginClicked = {
                    isError.value = !isError.value
                }
            )
        }
    }
}