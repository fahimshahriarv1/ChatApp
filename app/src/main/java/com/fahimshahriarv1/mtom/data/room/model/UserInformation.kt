package com.fahimshahriarv1.mtom.data.room.model

import com.fahimshahriarv1.mtom.domain.model.StatusEnum

data class UserInformation(
    val password: String = "",
    val status: StatusEnum = StatusEnum.NONE,
    val name: String = "",
    val userName: String = "",
    var usersConnected: List<String> = listOf(),
    val token: String = ""
)