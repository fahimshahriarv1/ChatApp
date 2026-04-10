package com.fahimshahriarv1.mtom.domain.repository

import kotlinx.coroutines.flow.Flow


interface LocalDatabaseRepository {
    fun getConnectedList(uName:String): Flow<List<String>>
    suspend fun saveConnectedList(userName: String, list: List<String>): Result<Unit>
}
