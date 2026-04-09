package com.fahimshahriarv1.mtom.domain.repository

import com.fahimshahriarv1.mtom.data.room.model.ChatUserEntity
import com.fahimshahriarv1.mtom.data.room.model.MessageInfoEntity
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getMessagesForChat(chatId: String): Flow<List<MessageInfoEntity>>
    fun getChatList(): Flow<List<ChatUserEntity>>
    suspend fun sendMessage(chatId: String, recipientId: String, senderId: String, message: String)
    suspend fun saveIncomingMessage(chatId: String, senderId: String, message: String, timestamp: Long)
}
