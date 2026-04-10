package com.fahimshahriarv1.mtom.domain.usecases.data

import com.fahimshahriarv1.mtom.domain.usecases.GetTokenUseCase
import com.fahimshahriarv1.mtom.domain.usecases.GetUserLoggedInStateUseCase
import com.fahimshahriarv1.mtom.domain.usecases.SaveMobileUseCase
import com.fahimshahriarv1.mtom.domain.usecases.SaveTokenUseCase

data class LocalUserLogin(
    val saveToken: SaveTokenUseCase,
    val saveMobileNumber: SaveMobileUseCase,
    val getToken: GetTokenUseCase,
    val getUSerLoggedInState: GetUserLoggedInStateUseCase
)
