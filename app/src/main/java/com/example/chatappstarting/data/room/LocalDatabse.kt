package com.example.chatappstarting.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.chatappstarting.BuildConfig
import com.example.chatappstarting.data.room.converters.Converters
import com.example.chatappstarting.data.room.model.ChatUserEntity
import com.example.chatappstarting.data.room.model.MessageInfoEntity

@Database(
    version = BuildConfig.VERSION_CODE,
    exportSchema = false,
    entities = [MessageInfoEntity::class, ChatUserEntity::class]
)

@TypeConverters(Converters::class)
abstract class AppDataBase : RoomDatabase() {

    abstract fun getMessageInfo(): MessageInfoDao

    abstract fun getUserList(): ChatUserDao

    companion object {
        private const val DATABASE_NAME = "lets_chat_data_base.db"

        fun create(context: Context): AppDataBase {
            val databaseBuilder =
                Room.databaseBuilder(context, AppDataBase::class.java, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()

            return databaseBuilder.build()
        }
    }
}