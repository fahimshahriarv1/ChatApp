package com.example.chatappstarting.domain.usecases

import com.example.chatappstarting.domain.manager.LocalUserManger
import javax.inject.Inject

class SaveConnectedUsersUseCase @Inject constructor(private val localUserManger: LocalUserManger) {
    suspend fun saveConnectedList(list: List<String>) {
        localUserManger.saveConnectedList(list)
    }
}