package com.fahimshahriarv1.mtom.di

import android.app.Application
import android.content.Context
import com.fahimshahriarv1.mtom.data.room.LocalDatabase
import com.fahimshahriarv1.mtom.presentation.navgraph.AppNavigator
import com.fahimshahriarv1.mtom.presentation.navgraph.AppNavigatorImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    @Provides
    @Singleton
    fun provideLocalDatabase(@ApplicationContext context: Context): LocalDatabase =
        LocalDatabase.create(context)
}