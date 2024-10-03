package com.example.chatappstarting.domain.usecases

import com.example.chatappstarting.domain.manager.LocalUserManger
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class LogoutUseCase @Inject constructor(private val localUserManger: LocalUserManger) {
    suspend fun logout() = useCaseHandler(Dispatchers.IO){
         localUserManger.clearPreferences().runCatching {  }
    }
}