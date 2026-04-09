package com.fahimshahriarv1.mtom.data.backup

import com.fahimshahriarv1.mtom.data.room.model.ChatUserEntity
import com.fahimshahriarv1.mtom.data.room.model.ConnectedUserEntity
import com.fahimshahriarv1.mtom.data.room.model.MessageInfoEntity

data class BackupData(
    val version: Int = 1,
    val timestamp: Long,
    val messages: List<MessageInfoEntity>,
    val chatUsers: List<ChatUserEntity>,
    val connectedUsers: List<ConnectedUserEntity>
)
