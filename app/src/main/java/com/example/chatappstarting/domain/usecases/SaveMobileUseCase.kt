package com.example.chatappstarting.domain.usecases

import com.example.chatappstarting.domain.manager.LocalUserManger
import javax.inject.Inject

class SaveMobileUseCase @Inject constructor(private val localUserManger: LocalUserManger) {
    suspend fun saveMobileNumber(number: String) {
        localUserManger.saveMobileNumber(number)
    }
}