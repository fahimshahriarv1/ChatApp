package com.example.chatappstarting.domain.usecases

import com.example.chatappstarting.data.room.model.UserInformation
import com.example.chatappstarting.domain.manager.LocalUserManger
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(private val localUserManger: LocalUserManger) {
    fun getUserInfo(): Flow<UserInformation> {
        return localUserManger.getUserInfo()
    }
}