package com.example.chatappstarting.domain.usecases

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach

inline fun <T> useCaseHandler(
    coroutineDispatcher: CoroutineDispatcher,
    crossinline useCase: suspend () -> Result<T>
): Flow<Result<T>> = flow<Result<T>> {
    runCatching {
        useCase()
    }.onSuccess {
        emit(it)
    }.onFailure {
        emit(Result.failure(it))
    }
}.flowOn(coroutineDispatcher)

inline fun <T> useCaseFlowHandler(
    coroutineDispatcher: CoroutineDispatcher,
    crossinline useCase: suspend () -> Flow<T>
): Flow<T> = flow {
    runCatching {
        useCase()
    }.onSuccess {
        it.onEach { value ->
            emit(value)
        }
    }.onFailure { }
}.flowOn(coroutineDispatcher)

inline fun <T> dataStoreHandler(
    context: Context,
    coroutineDispatcher: CoroutineDispatcher,
    key: Preferences.Key<T>,
    crossinline action: suspend (Context, Preferences.Key<T>) -> Result<T>
): Flow<Result<T>> = flow<Result<T>> {
    runCatching {
        action(context, key)
    }.onSuccess {
        emit(it)
    }.onFailure {
        emit(Result.failure(it))
    }
}.flowOn(coroutineDispatcher)

//// Function to set data in DataStore
// class StoreHandlers {
//    suspend fun <T> setDataInDataStore(
//        context: Context,
//        key: Preferences.Key<T>,
//        value: T
//    ): Result<Unit> {
//        return runCatching {
//            context.dataStore.edit { preferences ->
//                preferences[key] = value
//            }
//        }
//    }
//
//    // Function to get data from DataStore
//    suspend fun <T> getDataFromDataStore(
//        context: Context,
//        key: Preferences.Key<T>,
//        defaultValue: T
//    ): Result<T> {
//        return runCatching {
//            val preferences = context.dataStore.data.first()
//            preferences[key] ?: defaultValue
//        }
//    }
//}
