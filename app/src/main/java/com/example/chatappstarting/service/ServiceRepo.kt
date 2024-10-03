package com.example.chatappstarting.service

import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.chatappstarting.constants.USER_NAME
import javax.inject.Inject

class ServiceRepo @Inject constructor(private val context: Context) {

    fun startService(uname: String) {
        Thread {
            val intent = Intent(context, ServiceMain::class.java)
            intent.putExtra(USER_NAME, uname)
            intent.action = ServiceAction.START_SERVICE.name
            startServiceIntent(intent)
        }.start()
    }

    private fun startServiceIntent(intent: Intent) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            context.startForegroundService(intent)
        else
            context.startService(intent)
    }
}