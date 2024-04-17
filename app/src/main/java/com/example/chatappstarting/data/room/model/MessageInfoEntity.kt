package com.example.chatappstarting.data.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "all_chats"
)
data class MessageInfoEntity(
    @PrimaryKey @ColumnInfo(name = "time_stamp") val timeStamp: String,
    @ColumnInfo(name = "chat_id") val chatId: String,
    @ColumnInfo(name = "message") val message: String,
    @ColumnInfo(name = "sender_id") val senderId: String,
    @ColumnInfo(name = "message_id") val messageId: String,
    @ColumnInfo(name = "message_status") val messageStatus: String,
    @ColumnInfo(name = "previous_message_sender_id") val previousMessageSenderId: String,
    @ColumnInfo(name = "previous_message_id") val previousMessageId: String
)