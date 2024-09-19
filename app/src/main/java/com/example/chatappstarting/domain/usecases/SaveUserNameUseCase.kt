package com.example.chatappstarting.domain.usecases

import com.example.chatappstarting.domain.manager.LocalUserManger
import javax.inject.Inject

class SaveUserNameUseCase @Inject constructor(private val localUserManger: LocalUserManger) {
    suspend fun saveUserName(name: String) {
        localUserManger.setUserName(name)
    }
}