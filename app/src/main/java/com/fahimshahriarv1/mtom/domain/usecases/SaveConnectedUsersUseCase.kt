package com.fahimshahriarv1.mtom.domain.usecases

import com.fahimshahriarv1.mtom.domain.repository.LocalDatabaseRepository
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class SaveConnectedUsersUseCase @Inject constructor(private val localDatabaseRepository: LocalDatabaseRepository) {
    suspend fun saveConnectedList(uName: String, list: List<String>) =
        useCaseHandler(Dispatchers.IO) {
            localDatabaseRepository.saveConnectedList(uName, list)
        }
}