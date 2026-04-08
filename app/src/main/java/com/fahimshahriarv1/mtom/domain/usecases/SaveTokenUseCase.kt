package com.fahimshahriarv1.mtom.domain.usecases

import com.fahimshahriarv1.mtom.domain.repository.LocalUserRepository
import javax.inject.Inject

class SaveTokenUseCase @Inject constructor(private val localUserRepository: LocalUserRepository){
    suspend fun saveToken(token: String){
        localUserRepository.saveUserToken(token)
    }
}