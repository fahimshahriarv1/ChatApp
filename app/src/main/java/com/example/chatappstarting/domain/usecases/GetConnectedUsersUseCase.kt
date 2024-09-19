package com.example.chatappstarting.domain.usecases

import com.example.chatappstarting.domain.manager.LocalUserManger
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetConnectedUsersUseCase @Inject constructor(private val localUserManger: LocalUserManger) {
    fun getConnectionList(): Flow<List<String>> {
        return localUserManger.getConnectedList()
    }
}