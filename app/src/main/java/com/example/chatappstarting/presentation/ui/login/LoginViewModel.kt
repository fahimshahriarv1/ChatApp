package com.example.chatappstarting.presentation.ui.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {
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