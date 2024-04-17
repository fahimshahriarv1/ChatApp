package com.example.chatappstarting.domain.usecases

import com.example.chatappstarting.domain.manager.LocalUserManger

class SaveTokenUseCase (private val localUserManger: LocalUserManger){
    suspend fun saveToken(token: String){
        localUserManger.saveUserToken(token)
    }
}