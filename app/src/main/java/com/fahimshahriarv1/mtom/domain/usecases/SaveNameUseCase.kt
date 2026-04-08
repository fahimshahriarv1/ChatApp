package com.fahimshahriarv1.mtom.domain.usecases

import com.fahimshahriarv1.mtom.domain.repository.LocalUserRepository
import javax.inject.Inject

class SaveNameUseCase @Inject constructor(private val localUserRepository: LocalUserRepository) {
    suspend fun saveName(name: String) {
        localUserRepository.setUserNameOnly(name)
    }
}