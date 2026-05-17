package com.fahimshahriarv1.mtom.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import com.fahimshahriarv1.mtom.presentation.ui.home.HomeActivity
import com.fahimshahriarv1.mtom.R
import com.fahimshahriarv1.mtom.constants.USER_NAME
import com.fahimshahriarv1.mtom.data.crypto.CryptoManager
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

    @Inject
    lateinit var cryptoManager: CryptoManager

    // Track message history per sender for stacked notifications
    private val messageHistory = mutableMapOf<String, MutableList<NotificationCompat.MessagingStyle.Message>>()

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
        firebaseMessageManager.startListening(currentUser) { senderId, message, timestamp, isEcho, recipientId, sid ->
            serviceScope.launch {
                if (isEcho) {
                    // Skip echoes from this same session (we already have them locally)
                    if (sid == firebaseMessageManager.sessionId) {
                        Log.d(TAG, "Skipping own echo (same session)")
                        return@launch
                    }
                    // Echo from another device — save as sent message
                    val echoRecipient = recipientId ?: return@launch
                    val chatId = generateChatId(currentUser, echoRecipient)
                    chatRepository.saveSyncedSentMessage(chatId, senderId, echoRecipient, message, timestamp)
                    Log.d(TAG, "Saved synced sent message from another device to $echoRecipient")
                    return@launch
                }

                val chatId = generateChatId(currentUser, senderId)
                chatRepository.saveIncomingMessage(chatId, senderId, message, timestamp)

                // Decrypt for notification display
                val displayMessage = try {
                    val key = cryptoManager.deriveConversationKey(senderId, currentUser)
                    val payload = cryptoManager.decrypt(message, key)
                    payload.split("|", limit = 2)[0]
                } catch (_: Exception) {
                    message // Fallback for unencrypted messages
                }
                showMessageNotification(senderId, displayMessage)
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

        val currentUser = firebaseMessageManager.currentUser ?: ""
        val chatId = generateChatId(currentUser, from)

        // Accumulate messages per sender
        val sender = Person.Builder().setName(from).build()
        val newMessage = NotificationCompat.MessagingStyle.Message(
            body, System.currentTimeMillis(), sender
        )
        val messages = messageHistory.getOrPut(from) { mutableListOf() }
        messages.add(newMessage)

        val self = Person.Builder().setName("You").build()
        val style = NotificationCompat.MessagingStyle(self)
        messages.forEach { style.addMessage(it) }

        val intent = Intent(this, HomeActivity::class.java).apply {
            putExtra("chat_id", chatId)
            putExtra("recipient_name", from)
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            from.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Inline reply action
        val remoteInput = RemoteInput.Builder(ReplyReceiver.KEY_REPLY)
            .setLabel("Reply")
            .build()

        val replyIntent = Intent(this, ReplyReceiver::class.java).apply {
            putExtra("recipient_name", from)
            putExtra("current_user", currentUser)
            putExtra("notification_id", from.hashCode())
        }

        val replyPendingIntent = PendingIntent.getBroadcast(
            this,
            from.hashCode(),
            replyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        val replyAction = NotificationCompat.Action.Builder(
            R.drawable.lets_chat,
            "Reply",
            replyPendingIntent
        ).addRemoteInput(remoteInput).build()

        val notification = NotificationCompat.Builder(this, messageChannelId)
            .setSmallIcon(R.drawable.lets_chat)
            .setStyle(style)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(replyAction)
            .setNumber(messages.size)
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
