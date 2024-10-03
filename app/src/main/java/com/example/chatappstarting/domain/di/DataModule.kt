package com.example.chatappstarting.domain.di

import android.app.Application
import com.example.chatappstarting.data.manager.LocalDataBaseMangerImpl
import com.example.chatappstarting.data.manager.LocalUserMangerImpl
import com.example.chatappstarting.data.room.LocalDatabase
import com.example.chatappstarting.domain.manager.LocalDataBaseManger
import com.example.chatappstarting.domain.manager.LocalUserManger
import com.example.chatappstarting.domain.usecases.GetTokenUseCase
import com.example.chatappstarting.domain.usecases.GetUserLoggedInStateUseCase
import com.example.chatappstarting.domain.usecases.SaveMobileUseCase
import com.example.chatappstarting.domain.usecases.SaveTokenUseCase
import com.example.chatappstarting.domain.usecases.data.LocalUserLogin
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideLocalUserManger(application: Application): LocalUserManger =
        LocalUserMangerImpl(application)

    @Provides
    @Singleton
    fun provideLocalDatabaseManger(db: LocalDatabase): LocalDataBaseManger =
        LocalDataBaseMangerImpl(db)

    @Provides
    @Singleton
    fun provideLocalUserLogin(localUserManger: LocalUserManger): LocalUserLogin = LocalUserLogin(
        saveToken = SaveTokenUseCase(localUserManger),
        getToken = GetTokenUseCase(localUserManger),
        getUSerLoggedInState = GetUserLoggedInStateUseCase(localUserManger),
        saveMobileNumber = SaveMobileUseCase(localUserManger)
    )

    @Provides
    @Singleton
    fun provideUserLoginState(localUserManger: LocalUserManger): GetUserLoggedInStateUseCase =
        GetUserLoggedInStateUseCase(localUserManger)

    @Provides
    @Singleton
    fun provideSaveTokenUseCase(localUserManger: LocalUserManger): SaveTokenUseCase =
        SaveTokenUseCase(localUserManger)
}