package com.example.chatappstarting.presentation.ui.splashScreen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.chatappstarting.domain.usecases.data.LocalUserLogin
import com.example.chatappstarting.presentation.navgraph.Route
import com.example.chatappstarting.presentation.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val localUserLogin: LocalUserLogin
) : BaseViewModel() {
    val startDest = mutableStateOf("")

    init {
        viewModelScope.launch {
            localUserLogin.getUSerLoggedInState.getLoggedInState().onEach {
                delay(3000)
                startDest.value = if (it)
                    Route.AppMain.route
                else
                    Route.AppAuth.route
            }.launchIn(viewModelScope)
        }
    }
}