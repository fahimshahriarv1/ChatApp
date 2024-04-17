package com.example.chatappstarting.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.chatappstarting.data.room.model.ChatUserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatUserDao {
    @Query("SELECT * FROM chat_user_list")
    fun getChats(): Flow<List<ChatUserEntity>>

    @Query("UPDATE chat_user_list SET message = :message,time_stamp=:timeStamp,sender_id=:senderId,message_id=:messageId,message_status=:messageStatus WHERE chat_id =:id ")
    suspend fun updateLastChatWithUser(
        id: String,
        message: String,
        timeStamp: String,
        senderId: String,
        messageId: String,
        messageStatus: String
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewChatUser(chatUserEntity: ChatUserEntity)

    @Query("DELETE FROM chat_user_list WHERE chat_id = :id")
    suspend fun deleteUserAllChat(id: String)

    @Query("DELETE FROM chat_user_list")
    suspend fun deleteAllChat()
}