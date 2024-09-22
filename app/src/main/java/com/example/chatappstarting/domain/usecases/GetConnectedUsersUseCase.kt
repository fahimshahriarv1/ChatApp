package com.example.chatappstarting.domain.usecases

import com.example.chatappstarting.domain.manager.LocalDataBaseManger
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetConnectedUsersUseCase @Inject constructor(private val localDataBaseManger: LocalDataBaseManger) {
    fun getConnectionList(uName: String): Flow<List<String>> {
        return localDataBaseManger.getConnectedList(uName)
    }
}