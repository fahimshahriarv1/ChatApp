package com.example.chatappstarting.data.room.model

import com.example.chatappstarting.presentation.ui.home.model.StatusEnum

data class UserInfo(
    val password: String = "",
    private val status: String = "",
    val name: String = "",
    val user_name: String = "",
    private val users_connected: List<String> = listOf()
) {
    fun getConnectedUserNames(): List<String> {
        return users_connected
    }

    fun getStatus(): StatusEnum {
        return when (status) {
            "online" -> StatusEnum.ONLINE
            else -> StatusEnum.OFFLINE
        }
    }
}