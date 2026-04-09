package com.fahimshahriarv1.mtom.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.fahimshahriarv1.mtom.R
import com.fahimshahriarv1.mtom.constants.USER_NAME
import com.fahimshahriarv1.mtom.data.firebase.FirebaseMessageManager
import com.fahimshahriarv1.mtom.domain.repository.ChatRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ServiceMain : Service() {
    private val TAG = "ServiceMain"
    var flagIsRunning = false
    private lateinit var notificationManager: NotificationManager
    private val channelId = "channel1"
    private val messageChannelId = "messages"
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Inject
    lateinit var firebaseMessageManager: FirebaseMessageManager

    @Inject
    lateinit var chatRepository: ChatRepository

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NotificationManager::class.java)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let { incomingIntent ->
            when (incomingIntent.action) {
                ServiceAction.START_SERVICE.name -> handleIncomingIntent(intent)
                ServiceAction.STOP_SERVICE.name -> {
                    firebaseMessageManager.stopListening()
                    stopSelf()
                }
                else -> Unit
            }
        }

        return START_STICKY
    }

    private fun handleIncomingIntent(intent: Intent) {
        if (!flagIsRunning) {
            flagIsRunning = true
            val uname = intent.getStringExtra(USER_NAME) ?: ""
            startServiceWithNotification()
            if (uname.isNotEmpty()) {
                firebaseMessageManager.registerUser(uname) {
                    setupMessageListener(uname)
                }
            }
        }
    }

    private fun setupMessageListener(currentUser: String) {
        firebaseMessageManager.startListening(currentUser) { senderId, message, timestamp ->
            serviceScope.launch {
                val chatId = generateChatId(currentUser, senderId)
                chatRepository.saveIncomingMessage(chatId, senderId, message, timestamp)
                showMessageNotification(senderId, message)
            }
        }
        Log.d(TAG, "Firebase listener started for $currentUser")
    }

    private fun showMessageNotification(from: String, body: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                messageChannelId,
                "Messages",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, messageChannelId)
            .setSmallIcon(R.drawable.lets_chat)
            .setContentTitle(from)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(from.hashCode(), notification)
    }

    private fun generateChatId(user1: String, user2: String): String {
        return if (user1 < user2) "${user1}_$user2" else "${user2}_$user1"
    }

    private fun startServiceWithNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "MtoM Service",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)

            val builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.lets_chat)
                .setContentTitle("MtoM")
                .setContentText("Connected")
                .setPriority(NotificationCompat.PRIORITY_LOW)

            startForeground(1, builder.build())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        firebaseMessageManager.stopListening()
        serviceScope.cancel()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}
