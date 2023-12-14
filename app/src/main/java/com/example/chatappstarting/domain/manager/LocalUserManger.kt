package com.example.chatappstarting.domain.manager

import kotlinx.coroutines.flow.Flow

interface LocalUserManger {
    fun getUserToken(): Flow<String>
    fun getUserLoggedInState(): Flow<Boolean>
    suspend fun saveUserToken(token: String)
    suspend fun removeLoggedInState()
}