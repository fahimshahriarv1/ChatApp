package com.example.chatappstarting.domain.usecases

import com.example.chatappstarting.domain.manager.LocalUserManger
import kotlinx.coroutines.flow.Flow

class GetUSerLoggedInStateUseCase (private val localUserManger: LocalUserManger) {
    fun getLoggedInState(): Flow<Boolean> {
        return localUserManger.getUserLoggedInState()
    }
}