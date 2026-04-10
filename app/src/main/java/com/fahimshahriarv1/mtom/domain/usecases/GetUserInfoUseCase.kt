package com.fahimshahriarv1.mtom.domain.usecases

import com.fahimshahriarv1.mtom.data.room.model.UserInformation
import com.fahimshahriarv1.mtom.domain.repository.LocalUserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(private val localUserRepository: LocalUserRepository) {
    fun getUserInfo(): Flow<UserInformation> {
        return localUserRepository.getUserInfo()
    }
}