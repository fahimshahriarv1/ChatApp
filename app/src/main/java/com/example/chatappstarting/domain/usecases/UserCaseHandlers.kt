package com.example.chatappstarting.domain.usecases

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

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
