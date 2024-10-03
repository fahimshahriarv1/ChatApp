package com.example.chatappstarting.domain.manager

import com.example.chatappstarting.data.room.model.UserInformation
import kotlinx.coroutines.flow.Flow

interface LocalUserManger {
    fun getUserToken(): Flow<String>
    fun getUserLoggedInState(): Flow<Boolean>
    suspend fun saveUserToken(token: String)
    suspend fun removeLoggedInState()
    suspend fun saveMobileNumber(number: String)
    suspend fun clearPreferences()
    suspend fun getUserName(): Flow<String>
    suspend fun getUserNameOnly(): Flow<String>
    suspend fun setUserNameOnly(name: String)
    suspend fun setUserName(uName: String)
    fun getUserInfo(): Flow<UserInformation>
}