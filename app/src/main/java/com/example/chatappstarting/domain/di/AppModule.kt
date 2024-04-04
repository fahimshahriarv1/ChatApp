package com.example.chatappstarting.domain.di

import android.app.Activity
import android.app.Application
import android.content.Context
import com.example.chatappstarting.presentation.navgraph.AppNavigator
import com.example.chatappstarting.presentation.navgraph.AppNavigatorImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
    fun provideAppNavigator(): AppNavigator = AppNavigatorImpl()

    @Provides
    fun provideAppContext(application: Application): Context = application

    @Provides
    @Singleton
    fun provideFirebaseFireStore(): FirebaseFirestore = FirebaseFirestore.getInstance()


    @Provides
    @Singleton
    fun provideFirebaseFireBaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
}