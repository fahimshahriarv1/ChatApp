package com.example.chatappstarting.domain.manager

import kotlinx.coroutines.flow.Flow


interface LocalDataBaseManger {
    fun getConnectedList(uName:String): Flow<List<String>>
    suspend fun saveConnectedList(userName: String, list: List<String>): Result<Unit>
}