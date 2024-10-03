package com.example.chatappstarting.domain.usecases

import com.example.chatappstarting.domain.manager.LocalUserManger
import javax.inject.Inject

class SaveNameUseCase @Inject constructor(private val localUserManger: LocalUserManger) {
    suspend fun saveName(name: String) {
        localUserManger.setUserNameOnly(name)
    }
}