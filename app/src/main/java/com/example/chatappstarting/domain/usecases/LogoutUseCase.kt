package com.example.chatappstarting.domain.usecases

import com.example.chatappstarting.domain.manager.LocalUserManger
import javax.inject.Inject

class LogoutUseCase @Inject constructor(private val localUserManger: LocalUserManger) {
    suspend fun logout() {
        return localUserManger.clearPreferences()
    }
}