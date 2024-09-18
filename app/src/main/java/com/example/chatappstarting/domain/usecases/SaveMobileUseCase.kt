package com.example.chatappstarting.domain.usecases

import com.example.chatappstarting.domain.manager.LocalUserManger

class SaveMobileUseCase (private val localUserManger: LocalUserManger){
    suspend fun saveMobileNumber(number: String){
        localUserManger.saveMobileNumber(number)
    }
}