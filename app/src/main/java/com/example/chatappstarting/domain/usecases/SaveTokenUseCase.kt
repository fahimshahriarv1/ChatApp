package com.example.chatappstarting.domain.usecases

import com.example.chatappstarting.domain.manager.LocalUserManger
import javax.inject.Inject

class SaveTokenUseCase @Inject constructor(private val localUserManger: LocalUserManger){
    suspend fun saveToken(token: String){
        localUserManger.saveUserToken(token)
    }
}