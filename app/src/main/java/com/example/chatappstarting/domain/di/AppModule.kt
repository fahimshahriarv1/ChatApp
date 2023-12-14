package com.example.chatappstarting.domain.di

import android.app.Application
import com.example.chatappstarting.data.manager.LocalUserMangerImpl
import com.example.chatappstarting.domain.usecases.data.LocalUserLogin
import com.example.chatappstarting.domain.manager.LocalUserManger
import com.example.chatappstarting.domain.usecases.GetTokenUseCase
import com.example.chatappstarting.domain.usecases.GetUserLoggedInStateUseCase
import com.example.chatappstarting.domain.usecases.SaveTokenUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideLocalUserManger(application: Application): LocalUserManger =
        LocalUserMangerImpl(application)

    @Provides
    @Singleton
    fun provideLocalUserLogin(localUserManger: LocalUserManger): LocalUserLogin = LocalUserLogin(
        saveToken = SaveTokenUseCase(localUserManger),
        getToken = GetTokenUseCase(localUserManger),
        getUSerLoggedInState = GetUserLoggedInStateUseCase(localUserManger)
    )

    @Provides
    fun provideGetUserLoginState(localUserManger: LocalUserManger): GetUserLoggedInStateUseCase =
        GetUserLoggedInStateUseCase(localUserManger)

    @Provides
    fun provideSaveTokenUseCase(localUserManger: LocalUserManger): SaveTokenUseCase =
        SaveTokenUseCase(localUserManger)
}