package com.fahimshahriarv1.mtom.domain.usecases

import com.fahimshahriarv1.mtom.domain.repository.LocalUserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserLoggedInStateUseCase @Inject constructor(
    private val localUserRepository: LocalUserRepository
) {
    fun getLoggedInState(): Flow<Boolean> {
        return localUserRepository.getUserLoggedInState()
    }
}