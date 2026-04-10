package com.fahimshahriarv1.mtom.service

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import com.fahimshahriarv1.mtom.R
import com.fahimshahriarv1.mtom.domain.repository.ChatRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReplyReceiver : BroadcastReceiver() {

    @Inject
    lateinit var chatRepository: ChatRepository

    override fun onReceive(context: Context, intent: Intent) {
        val remoteInput = RemoteInput.getResultsFromIntent(intent)
        val replyText = remoteInput?.getCharSequence(KEY_REPLY)?.toString() ?: return

        val recipientName = intent.getStringExtra("recipient_name") ?: return
        val currentUser = intent.getStringExtra("current_user") ?: return
        val notificationId = intent.getIntExtra("notification_id", 0)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val chatId = generateChatId(currentUser, recipientName)
                chatRepository.sendMessage(chatId, recipientName, currentUser, replyText)

                // Build updated notification with reply confirmation
                val self = Person.Builder().setName("You").build()
                val style = NotificationCompat.MessagingStyle(self)
                    .addMessage(replyText, System.currentTimeMillis(), self)

                val updatedNotification = NotificationCompat.Builder(context, "messages")
                    .setSmallIcon(R.drawable.lets_chat)
                    .setStyle(style)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setAutoCancel(true)
                    .build()

                notificationManager.notify(notificationId, updatedNotification)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun generateChatId(user1: String, user2: String): String {
        return if (user1 < user2) "${user1}_$user2" else "${user2}_$user1"
    }

    companion object {
        const val KEY_REPLY = "key_reply"
    }
}
