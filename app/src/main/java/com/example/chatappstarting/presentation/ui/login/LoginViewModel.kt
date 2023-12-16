package com.example.chatappstarting.presentation.ui.login

import androidx.compose.runtime.mutableStateOf
import com.example.chatappstarting.presentation.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : BaseViewModel() {
    val uname = mutableStateOf("")
    val pass = mutableStateOf("")
    val isError = mutableStateOf(false)

    fun onUnameChanged(name: String) {
        uname.value = name
    }

    fun onPassChanged(pwd: String) {
        pass.value = pwd
    }

    fun onLoginClicked() {
        isError.value = !isError.value
    }
}