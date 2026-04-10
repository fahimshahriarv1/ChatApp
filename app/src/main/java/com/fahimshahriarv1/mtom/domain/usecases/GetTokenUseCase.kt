package com.fahimshahriarv1.mtom.domain.usecases

import com.fahimshahriarv1.mtom.domain.repository.LocalUserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTokenUseCase @Inject constructor(private val localUserRepository: LocalUserRepository) {
    fun getToken(): Flow<String> {
        return localUserRepository.getUserToken()
    }
}