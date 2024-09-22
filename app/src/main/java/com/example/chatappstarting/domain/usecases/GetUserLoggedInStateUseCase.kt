package com.example.chatappstarting.domain.usecases

import com.example.chatappstarting.domain.manager.LocalUserManger
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserLoggedInStateUseCase @Inject constructor(
    private val localUserManger: LocalUserManger
) {
    fun getLoggedInState(): Flow<Boolean> {
        return localUserManger.getUserLoggedInState()
    }
}