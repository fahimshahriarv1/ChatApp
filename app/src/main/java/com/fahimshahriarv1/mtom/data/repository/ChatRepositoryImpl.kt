package com.fahimshahriarv1.mtom.data.repository

import android.util.Log
import com.fahimshahriarv1.mtom.data.crypto.CryptoManager
import com.fahimshahriarv1.mtom.data.firebase.FirebaseMessageManager
import com.fahimshahriarv1.mtom.data.room.ChatUserDao
import com.fahimshahriarv1.mtom.data.room.MessageInfoDao
import com.fahimshahriarv1.mtom.data.room.model.ChatUserEntity
import com.fahimshahriarv1.mtom.data.room.model.MessageInfoEntity
import com.fahimshahriarv1.mtom.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.UUID
import kotlin.coroutines.resume

class ChatRepositoryImpl(
    private val messageInfoDao: MessageInfoDao,
    private val chatUserDao: ChatUserDao,
    private val firebaseMessageManager: FirebaseMessageManager,
    private val cryptoManager: CryptoManager
) : ChatRepository {

    override fun getMessagesForChat(chatId: String): Flow<List<MessageInfoEntity>> {
        return messageInfoDao.getMessageInfo(chatId)
    }

    override fun getChatList(): Flow<List<ChatUserEntity>> {
        return chatUserDao.getChats()
    }

    override suspend fun sendMessage(
        chatId: String,
        recipientId: String,
        senderId: String,
        message: String
    ) {
        val timestamp = System.currentTimeMillis().toString()
        val messageId = UUID.randomUUID().toString()

        val messageEntity = MessageInfoEntity(
            timeStamp = timestamp,
            chatId = chatId,
            message = message,
            senderId = senderId,
            messageId = messageId,
            messageStatus = "sent",
            previousMessageSenderId = "",
            previousMessageId = ""
        )

        messageInfoDao.insertNewMessage(messageEntity)

        chatUserDao.insertNewChatUser(
            ChatUserEntity(
                chatId = chatId,
                timeStamp = timestamp,
                message = message,
                senderId = senderId,
                messageId = messageId,
                messageStatus = "sent"
            )
        )

        // Encrypt message + timestamp together before sending via Firebase RTDB
        val key = cryptoManager.deriveConversationKey(senderId, recipientId)
        val payload = "$message|$timestamp"
        val encryptedMessage = cryptoManager.encrypt(payload, key)
        Log.d("E2EE", "Encrypted message+timestamp for $recipientId")

        val result = suspendCancellableCoroutine { cont ->
            firebaseMessageManager.sendMessage(recipientId, senderId, encryptedMessage) { result ->
                cont.resume(result)
            }
        }

        if (result.isFailure) {
            throw result.exceptionOrNull() ?: Exception("Failed to send message")
        }
    }

    override suspend fun saveIncomingMessage(
        chatId: String,
        senderId: String,
        message: String,
        timestamp: Long
    ) {
        // Decrypt incoming payload (message|timestamp)
        val currentUser = chatId.split("_").first { it != senderId }
        val (decryptedMessage, ts) = try {
            val key = cryptoManager.deriveConversationKey(senderId, currentUser)
            val payload = cryptoManager.decrypt(message, key)
            val parts = payload.split("|", limit = 2)
            Log.d("E2EE", "Decrypted message+timestamp from $senderId")
            parts[0] to (if (parts.size > 1) parts[1] else timestamp.toString())
        } catch (e: Exception) {
            Log.e("E2EE", "Decryption failed, using raw message: ${e.message}")
            message to timestamp.toString()
        }

        val messageId = UUID.randomUUID().toString()

        val messageEntity = MessageInfoEntity(
            timeStamp = ts,
            chatId = chatId,
            message = decryptedMessage,
            senderId = senderId,
            messageId = messageId,
            messageStatus = "received",
            previousMessageSenderId = "",
            previousMessageId = ""
        )

        messageInfoDao.insertNewMessage(messageEntity)

        chatUserDao.insertNewChatUser(
            ChatUserEntity(
                chatId = chatId,
                timeStamp = ts,
                message = decryptedMessage,
                senderId = senderId,
                messageId = messageId,
                messageStatus = "received"
            )
        )

        chatUserDao.incrementUnreadCount(chatId)
    }

    override suspend fun saveSyncedSentMessage(
        chatId: String,
        senderId: String,
        recipientId: String,
        message: String,
        timestamp: Long
    ) {
        val (decryptedMessage, ts) = try {
            val key = cryptoManager.deriveConversationKey(senderId, recipientId)
            val payload = cryptoManager.decrypt(message, key)
            val parts = payload.split("|", limit = 2)
            Log.d("E2EE", "Decrypted synced sent message")
            parts[0] to (if (parts.size > 1) parts[1] else timestamp.toString())
        } catch (e: Exception) {
            Log.e("E2EE", "Decrypt failed for synced sent message: ${e.message}")
            message to timestamp.toString()
        }

        val messageId = UUID.randomUUID().toString()

        messageInfoDao.insertNewMessage(
            MessageInfoEntity(
                timeStamp = ts,
                chatId = chatId,
                message = decryptedMessage,
                senderId = senderId,
                messageId = messageId,
                messageStatus = "sent",
                previousMessageSenderId = "",
                previousMessageId = ""
            )
        )

        chatUserDao.insertNewChatUser(
            ChatUserEntity(
                chatId = chatId,
                timeStamp = ts,
                message = decryptedMessage,
                senderId = senderId,
                messageId = messageId,
                messageStatus = "sent"
            )
        )
    }

    override suspend fun resetUnreadCount(chatId: String) {
        chatUserDao.resetUnreadCount(chatId)
    }
}
