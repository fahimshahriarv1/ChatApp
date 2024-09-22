package com.example.chatappstarting.data.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_list"
)
data class ConnectedUserEntity(
    @PrimaryKey @ColumnInfo(name = "user_name") val userName: String,
    @ColumnInfo(name = "users_connected") val usersConnected: List<String>
)