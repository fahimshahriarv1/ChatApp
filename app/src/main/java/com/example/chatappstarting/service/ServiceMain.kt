package com.example.chatappstarting.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.chatappstarting.R
import com.example.chatappstarting.constants.USER_NAME

class ServiceMain : Service() {
    var flagIsRunning = false
    private lateinit var notificationManager: NotificationManager
    private val channelId = "channel1"

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NotificationManager::class.java)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let { incomingIntent ->
            when (incomingIntent.action) {
                ServiceAction.START_SERVICE.name -> handleIncomingIntent(intent)
                else -> Unit
            }

        }

        return START_STICKY
    }

    private fun handleIncomingIntent(intent: Intent) {
        if (!flagIsRunning) {
            flagIsRunning = true
            val uname = intent.getStringExtra(USER_NAME)
            startServiceWithNotification()
        }
    }

    private fun startServiceWithNotification() {
        val channel: NotificationChannel
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            channel = NotificationChannel(
                channelId,
                "forGround",
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationManager.createNotificationChannel(channel)

            val builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.lets_chat)
                .setContentTitle("service started")
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            startForeground(1, builder.build())
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}