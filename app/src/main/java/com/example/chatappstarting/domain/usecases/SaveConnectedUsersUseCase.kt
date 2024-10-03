package com.example.chatappstarting.domain.usecases

import com.example.chatappstarting.domain.manager.LocalDataBaseManger
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class SaveConnectedUsersUseCase @Inject constructor(private val localDataBaseManger: LocalDataBaseManger) {
    suspend fun saveConnectedList(uName: String, list: List<String>) =
        useCaseHandler(Dispatchers.IO) {
            localDataBaseManger.saveConnectedList(uName, list)
        }
}