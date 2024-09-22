package com.example.chatappstarting.data.manager

import android.util.Log
import com.example.chatappstarting.data.room.LocalDatabase
import com.example.chatappstarting.data.room.model.ConnectedUserEntity
import com.example.chatappstarting.domain.manager.LocalDataBaseManger
import kotlinx.coroutines.flow.Flow

class LocalDataBaseMangerImpl(private val db: LocalDatabase) : LocalDataBaseManger {
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