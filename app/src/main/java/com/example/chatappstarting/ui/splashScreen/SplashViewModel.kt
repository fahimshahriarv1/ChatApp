package com.example.chatappstarting.ui.splashScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatappstarting.domain.usecases.data.LocalUserLogin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val localUserLogin: LocalUserLogin
) : ViewModel() {

    private val _isUserLoggedIn = MutableSharedFlow<Boolean>()
    val isUserLoggedIn = _isUserLoggedIn.asSharedFlow()

    fun saveLoginState() {
        viewModelScope.launch {
            localUserLogin.saveToken.saveToken("loggedIn")
        }
    }

    fun getLoggedInState() {
        viewModelScope.launch {
            localUserLogin.getUSerLoggedInState.getLoggedInState().collect {
                _isUserLoggedIn.emit(it)
            }
        }
    }
}