package com.fahimshahriarv1.mtom.domain.usecases

import com.fahimshahriarv1.mtom.domain.repository.LocalUserRepository
import javax.inject.Inject

class SaveUserNameUseCase @Inject constructor(private val localUserRepository: LocalUserRepository) {
    suspend fun saveUserName(name: String) {
        localUserRepository.setUserName(name)
    }
}