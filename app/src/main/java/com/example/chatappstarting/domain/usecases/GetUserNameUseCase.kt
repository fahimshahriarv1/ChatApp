package com.example.chatappstarting.domain.usecases

import com.example.chatappstarting.domain.manager.LocalUserManger
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserNameUseCase @Inject constructor(private val localUserManger: LocalUserManger) {
    suspend fun getUserName(): Flow<String> {
        return localUserManger.getUserName()
    }
}