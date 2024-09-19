package com.example.chatappstarting.domain.manager

import kotlinx.coroutines.flow.Flow

interface LocalUserManger {
    fun getUserToken(): Flow<String>
    fun getUserLoggedInState(): Flow<Boolean>
    suspend fun saveUserToken(token: String)
    suspend fun removeLoggedInState()
    suspend fun saveMobileNumber(number: String)
    suspend fun saveConnectedList(list: List<String>)
    fun getConnectedList(): Flow<List<String>>
    suspend fun clearPreferences()
    suspend fun getUserName(): Flow<String>
    suspend fun getUserNameOnly(): Flow<String>
    suspend fun setUserNameOnly(name: String)
    suspend fun setUserName(uName: String)
}