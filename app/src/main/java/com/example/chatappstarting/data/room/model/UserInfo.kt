package com.example.chatappstarting.data.room.model

data class UserInfo(
    val password: String = "",
    val status: String = "",
    val name: String = "",
    val user_name: String = "",
    private val users_connected: List<String> = listOf()
) {
    fun getConnectedUserNames(): List<String> {
        return users_connected
    }
}