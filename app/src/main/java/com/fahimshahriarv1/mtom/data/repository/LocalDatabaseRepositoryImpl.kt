package com.fahimshahriarv1.mtom.data.repository

import android.util.Log
import com.fahimshahriarv1.mtom.data.room.LocalDatabase
import com.fahimshahriarv1.mtom.data.room.model.ConnectedUserEntity
import com.fahimshahriarv1.mtom.domain.repository.LocalDatabaseRepository
import kotlinx.coroutines.flow.Flow

class LocalDatabaseRepositoryImpl(private val db: LocalDatabase) : LocalDatabaseRepository {
    override suspend fun saveConnectedList(userName: String, list: List<String>): Result<Unit> {
        Log.d("operation", "saving list + $db")
        return db.getUserConnectionList().saveConnectionList(ConnectedUserEntity(userName, list))
            .runCatching {
                Log.d("operation", "getiing list + $list")
            }
    }

    override fun getConnectedList(uName: String): Flow<List<String>> {
        Log.d("operation", "getiing list + $db")
        return db.getUserConnectionList().getConnectionList(uName)
    }
}
