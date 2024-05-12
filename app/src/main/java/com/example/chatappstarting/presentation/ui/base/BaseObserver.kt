package com.example.chatappstarting.presentation.ui.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.flow.Flow

@Composable
fun <T> FlowObserver(
    flow: Flow<T>,
    observer: (T) -> Unit
) {
    LaunchedEffect(flow) {
        flow.collect { value ->
            observer(value)
        }
    }
}


@Composable
fun <T> LiveDataObserver(
    liveData: LiveData<T>,
    owner: LifecycleOwner = LocalLifecycleOwner.current,
    observer: (T) -> Unit
) {
    val observerWrapper = rememberUpdatedState(newValue = observer)

    LaunchedEffect(liveData) {
        val liveDataObserver = Observer<T> { value ->
            observerWrapper.value(value)
        }
        liveData.observe(owner, liveDataObserver)
    }
}