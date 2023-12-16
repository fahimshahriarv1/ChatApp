package com.example.chatappstarting.presentation.ui.base

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor() : ViewModel() {
    val loaderState = mutableStateOf(false)
    val handler = CoroutineExceptionHandler { _, exception ->
        loaderState.value = false
        run {
            //toastMessage.value = Event(exception.message.toString())
            exception.printStackTrace()
            //Log.e(TAG, "Caught $exception")
        }
    }
}