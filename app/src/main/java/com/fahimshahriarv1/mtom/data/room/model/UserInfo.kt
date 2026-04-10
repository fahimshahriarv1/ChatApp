package com.fahimshahriarv1.mtom.data.room.model

import com.fahimshahriarv1.mtom.domain.model.StatusEnum

data class UserInfo(
    val password: String = "",
    val status: String = "",
    val name: String = "",
    val user_name: String = "",
    val users_connected: List<String> = listOf(),
    val token: String = ""
)
