package com.fahimshahriarv1.mtom.di

import android.app.Application
import com.fahimshahriarv1.mtom.data.crypto.CryptoManager
import com.fahimshahriarv1.mtom.data.firebase.FirebaseMessageManager
import com.fahimshahriarv1.mtom.data.repository.ChatRepositoryImpl
import com.fahimshahriarv1.mtom.data.repository.LocalDatabaseRepositoryImpl
import com.fahimshahriarv1.mtom.data.repository.LocalUserRepositoryImpl
import com.fahimshahriarv1.mtom.data.room.LocalDatabase
import com.fahimshahriarv1.mtom.domain.repository.ChatRepository
import com.fahimshahriarv1.mtom.domain.repository.LocalDatabaseRepository
import com.fahimshahriarv1.mtom.domain.repository.LocalUserRepository
import com.fahimshahriarv1.mtom.domain.usecases.GetTokenUseCase
import com.fahimshahriarv1.mtom.domain.usecases.GetUserLoggedInStateUseCase
import com.fahimshahriarv1.mtom.domain.usecases.SaveMobileUseCase
import com.fahimshahriarv1.mtom.domain.usecases.SaveTokenUseCase
import com.fahimshahriarv1.mtom.domain.usecases.data.LocalUserLogin
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
    fun provideLocalUserRepository(application: Application): LocalUserRepository =
        LocalUserRepositoryImpl(application)

    @Provides
    @Singleton
    fun provideLocalDatabaseManger(db: LocalDatabase): LocalDatabaseRepository =
        LocalDatabaseRepositoryImpl(db)

    @Provides
    @Singleton
    fun provideLocalUserLogin(localUserRepository: LocalUserRepository): LocalUserLogin = LocalUserLogin(
        saveToken = SaveTokenUseCase(localUserRepository),
        getToken = GetTokenUseCase(localUserRepository),
        getUSerLoggedInState = GetUserLoggedInStateUseCase(localUserRepository),
        saveMobileNumber = SaveMobileUseCase(localUserRepository)
    )

    @Provides
    @Singleton
    fun provideUserLoginState(localUserRepository: LocalUserRepository): GetUserLoggedInStateUseCase =
        GetUserLoggedInStateUseCase(localUserRepository)

    @Provides
    @Singleton
    fun provideSaveTokenUseCase(localUserRepository: LocalUserRepository): SaveTokenUseCase =
        SaveTokenUseCase(localUserRepository)

    @Provides
    @Singleton
    fun provideChatRepository(
        db: LocalDatabase,
        firebaseMessageManager: FirebaseMessageManager,
        cryptoManager: CryptoManager
    ): ChatRepository = ChatRepositoryImpl(
        messageInfoDao = db.getMessageInfo(),
        chatUserDao = db.getUserList(),
        firebaseMessageManager = firebaseMessageManager,
        cryptoManager = cryptoManager
    )
}
