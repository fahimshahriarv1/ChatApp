package com.example.chatappstarting.data.room.model

import com.example.chatappstarting.presentation.ui.home.model.StatusEnum

data class UserInfo(
    val password: String = "",
    val status: String = "",
    val name: String = "",
    val user_name: String = "",
    val users_connected: List<String> = listOf(),
    val token: String = ""
)
