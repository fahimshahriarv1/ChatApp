package com.fahimshahriarv1.mtom.domain.usecases

import com.fahimshahriarv1.mtom.domain.repository.LocalDatabaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetConnectedUsersUseCase @Inject constructor(private val localDatabaseRepository: LocalDatabaseRepository) {
    fun getConnectionList(uName: String): Flow<List<String>> {
        return localDatabaseRepository.getConnectedList(uName)
    }
}