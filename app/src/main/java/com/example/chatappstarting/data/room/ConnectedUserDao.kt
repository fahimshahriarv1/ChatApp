package com.example.chatappstarting.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.chatappstarting.data.room.model.ConnectedUserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConnectedUserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveConnectionList(userConnections: ConnectedUserEntity)

    @Query("SELECT users_connected FROM user_list WHERE user_name =:userName")
    fun getConnectionList(userName: String): Flow<List<String>>
}