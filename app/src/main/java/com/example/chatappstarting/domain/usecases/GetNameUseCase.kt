package com.example.chatappstarting.domain.usecases

import com.example.chatappstarting.domain.manager.LocalUserManger
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNameUseCase @Inject constructor(private val localUserManger: LocalUserManger) {
    suspend fun getName(): Flow<String> {
        return localUserManger.getUserNameOnly()
    }
}