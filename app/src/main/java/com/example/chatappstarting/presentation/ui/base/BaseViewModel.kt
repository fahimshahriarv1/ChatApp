package com.example.chatappstarting.presentation.ui.base

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatappstarting.presentation.navgraph.AppNavigator
import com.example.chatappstarting.presentation.navgraph.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor(protected val appNavigator: AppNavigator) :
    ViewModel() {
    val navChannel = appNavigator.navigationChannel
    val loaderState = mutableStateOf(false)

    private val _showToast = MutableSharedFlow<String?>()
    val showToast = _showToast.asSharedFlow()

    val handler = CoroutineExceptionHandler { _, exception ->
        loaderState.value = false
        run {
            //toastMessage.value = Event(exception.message.toString())
            exception.printStackTrace()
            //Log.e(TAG, "Caught $exception")
        }
    }

    fun showToast(msg: String) {
        viewModelScope.launch {
            _showToast.emit(msg)

            delay(5100)

            _showToast.emit(null)
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