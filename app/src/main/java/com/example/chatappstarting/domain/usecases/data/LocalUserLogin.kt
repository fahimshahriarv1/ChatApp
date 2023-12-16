package com.example.chatappstarting.domain.usecases.data

import com.example.chatappstarting.domain.usecases.GetTokenUseCase
import com.example.chatappstarting.domain.usecases.GetUserLoggedInStateUseCase
import com.example.chatappstarting.domain.usecases.SaveTokenUseCase

data class LocalUserLogin(
    val saveToken: SaveTokenUseCase,
    val getToken: GetTokenUseCase,
    val getUSerLoggedInState: GetUserLoggedInStateUseCase
)
