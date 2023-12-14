package com.example.chatappstarting.presentation.ui.splashScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatappstarting.domain.usecases.data.LocalUserLogin
import com.example.chatappstarting.presentation.navgraph.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val localUserLogin: LocalUserLogin
) : ViewModel() {
    var startDest by mutableStateOf("")
        private set

    init {
        viewModelScope.launch {
            localUserLogin.getUSerLoggedInState.getLoggedInState().onEach {
                delay(3000)
                startDest = if (!it)
                    Route.HomeScreen.route
                else
                    Route.AppAuth.route
            }.launchIn(viewModelScope)
        }
    }

    fun saveLoginState() {
        viewModelScope.launch {
            localUserLogin.saveToken.saveToken("loggedIn")
        }
    }
}