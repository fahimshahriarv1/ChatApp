package com.example.chatappstarting.data.room.model

import com.example.chatappstarting.presentation.ui.home.model.StatusEnum

data class UserInformation(
    val password: String = "",
    val status: StatusEnum = StatusEnum.NONE,
    val name: String = "",
    val userName: String = "",
    var usersConnected: List<String> = listOf(),
    val token: String = ""
)