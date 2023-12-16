package com.example.chatappstarting.presentation.ui.base

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

@Composable
fun ComposableLifecycle(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onEvent: (LifecycleOwner, Lifecycle.Event) -> Unit
) {

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { source, event ->
            onEvent(source, event)
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
fun ComposableLieCycleImpl(
    tag: String = "",
    onCreate: () -> Unit = {},
    onStart: () -> Unit = {},
    onResume: () -> Unit = {},
    onPause: () -> Unit = {},
    onStop: () -> Unit = {},
    onDestroy: () -> Unit = {}
) {
    ComposableLifecycle { _, event ->
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                Log.d(tag, "On_Create")
                onCreate()
            }

            Lifecycle.Event.ON_START -> {
                Log.d(tag, "On_Start")
                onStart()
            }

            Lifecycle.Event.ON_RESUME -> {
                Log.d(tag, "On_Resume")
                onResume()
            }

            Lifecycle.Event.ON_PAUSE -> {
                Log.d(tag, "On_Pause")
                onPause()
            }

            Lifecycle.Event.ON_STOP -> {
                Log.d(tag, "On_Stop")
                onStop()
            }

            Lifecycle.Event.ON_DESTROY -> {
                Log.d(tag, "On_Destroy")
                onDestroy()
            }

            else -> {}
        }
    }
}