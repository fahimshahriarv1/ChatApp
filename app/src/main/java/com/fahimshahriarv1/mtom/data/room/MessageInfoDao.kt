package com.fahimshahriarv1.mtom.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fahimshahriarv1.mtom.data.room.model.ChatUserEntity
import com.fahimshahriarv1.mtom.data.room.model.MessageInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageInfoDao {
    @Query("SELECT * FROM all_chats WHERE chat_id = :id ORDER BY CAST(time_stamp AS INTEGER) ASC")
    fun getMessageInfo(id: String): Flow<List<MessageInfoEntity>>

    @Query("DELETE FROM all_chats WHERE time_stamp = :timeStamp AND chat_id = :chatId AND sender_id=:senderId")
    suspend fun deleteSpecificAllChats(timeStamp: String, chatId: String, senderId: String)

    @Query("DELETE FROM all_chats")
    suspend fun deleteAllChat()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNewMessage(messageInfoEntity: MessageInfoEntity)

    @Query("SELECT * FROM all_chats")
    suspend fun getAllMessages(): List<MessageInfoEntity>
}