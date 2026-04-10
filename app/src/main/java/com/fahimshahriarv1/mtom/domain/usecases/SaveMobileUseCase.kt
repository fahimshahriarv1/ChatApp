package com.fahimshahriarv1.mtom.domain.usecases

import com.fahimshahriarv1.mtom.domain.repository.LocalUserRepository
import javax.inject.Inject

class SaveMobileUseCase @Inject constructor(private val localUserRepository: LocalUserRepository) {
    suspend fun saveMobileNumber(number: String) {
        localUserRepository.saveMobileNumber(number)
    }
}