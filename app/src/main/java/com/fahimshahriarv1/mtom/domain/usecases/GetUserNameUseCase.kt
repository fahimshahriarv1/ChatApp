package com.fahimshahriarv1.mtom.domain.usecases

import com.fahimshahriarv1.mtom.domain.repository.LocalUserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserNameUseCase @Inject constructor(private val localUserRepository: LocalUserRepository) {
    suspend fun getUserName(): Flow<String> {
        return localUserRepository.getUserName()
    }
}