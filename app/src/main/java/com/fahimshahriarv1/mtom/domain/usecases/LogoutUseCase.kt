package com.fahimshahriarv1.mtom.domain.usecases

import com.fahimshahriarv1.mtom.domain.repository.LocalUserRepository
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class LogoutUseCase @Inject constructor(private val localUserRepository: LocalUserRepository) {
    suspend fun logout() = useCaseHandler(Dispatchers.IO){
         localUserRepository.clearPreferences().runCatching {  }
    }
}