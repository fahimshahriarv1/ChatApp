package com.example.chatappstarting.domain.usecases

import com.example.chatappstarting.domain.manager.LocalUserManger
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTokenUseCase @Inject constructor(private val localUserManger: LocalUserManger) {
    fun getToken(): Flow<String> {
        return localUserManger.getUserToken()
    }
}