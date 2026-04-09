package com.fahimshahriarv1.mtom.data.repository

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
    private val firebaseMessageManager: FirebaseMessageManager
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

        // Send via Firebase RTDB
        val result = suspendCancellableCoroutine { cont ->
            firebaseMessageManager.sendMessage(recipientId, senderId, message) { result ->
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
        val ts = timestamp.toString()
        val messageId = UUID.randomUUID().toString()

        val messageEntity = MessageInfoEntity(
            timeStamp = ts,
            chatId = chatId,
            message = message,
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
                message = message,
                senderId = senderId,
                messageId = messageId,
                messageStatus = "received"
            )
        )

        chatUserDao.incrementUnreadCount(chatId)
    }

    override suspend fun resetUnreadCount(chatId: String) {
        chatUserDao.resetUnreadCount(chatId)
    }
}
