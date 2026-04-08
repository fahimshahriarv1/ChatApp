package com.fahimshahriarv1.mtom.domain.usecases

import com.fahimshahriarv1.mtom.domain.repository.LocalUserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNameUseCase @Inject constructor(private val localUserRepository: LocalUserRepository) {
    suspend fun getName(): Flow<String> {
        return localUserRepository.getUserNameOnly()
    }
}