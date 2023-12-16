package com.example.chatappstarting.presentation.ui.base

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatappstarting.presentation.navgraph.AppNavigator
import com.example.chatappstarting.presentation.navgraph.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor(protected val appNavigator: AppNavigator) :
    ViewModel() {
    val navChannel = appNavigator.navigationChannel
    val loaderState = mutableStateOf(false)
    val handler = CoroutineExceptionHandler { _, exception ->
        loaderState.value = false
        run {
            //toastMessage.value = Event(exception.message.toString())
            exception.printStackTrace()
            //Log.e(TAG, "Caught $exception")
        }
    }

    protected fun navigateTo(
        route: String,
        popUpToRoute: Route? = null,
        inclusive: Boolean = false,
        isSingleTop: Boolean = false
    ) {
        viewModelScope.launch {
            appNavigator.navigateTo(
                route,
                popUpToRoute?.route,
                inclusive,
                isSingleTop
            )
        }
    }
}