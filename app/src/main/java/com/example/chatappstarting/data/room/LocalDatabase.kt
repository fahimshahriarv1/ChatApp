package com.example.chatappstarting.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.chatappstarting.data.room.converters.Converters
import com.example.chatappstarting.data.room.model.ChatUserEntity
import com.example.chatappstarting.data.room.model.ConnectedUserEntity
import com.example.chatappstarting.data.room.model.MessageInfoEntity

@Database(
    version = 1,
    exportSchema = false,
    entities = [MessageInfoEntity::class, ChatUserEntity::class, ConnectedUserEntity::class]
)

@TypeConverters(Converters::class)
abstract class LocalDatabase : RoomDatabase() {

    abstract fun getMessageInfo(): MessageInfoDao

    abstract fun getUserList(): ChatUserDao

    abstract fun getUserConnectionList(): ConnectedUserDao

    companion object {
        private const val DATABASE_NAME = "lets_chat_data_base.db"

        fun create(context: Context): LocalDatabase {
            val databaseBuilder =
                Room.databaseBuilder(context, LocalDatabase::class.java, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()

            return databaseBuilder.build()
        }
    }
}