package com.example.chatappstarting.presentation.ui.common

import androidx.lifecycle.viewModelScope
import com.example.chatappstarting.data.room.model.UserInformation
import com.example.chatappstarting.domain.usecases.GetNameUseCase
import com.example.chatappstarting.domain.usecases.GetTokenUseCase
import com.example.chatappstarting.domain.usecases.GetUserNameUseCase
import com.example.chatappstarting.presentation.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class CommonViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var getUserNameUseCase: GetUserNameUseCase

    @Inject
    lateinit var getNameUseCase: GetNameUseCase

    @Inject
    lateinit var getTokenUseCase: GetTokenUseCase

    private val _userName = MutableStateFlow("")
    val userName = _userName.asStateFlow()

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _token = MutableStateFlow("")
    val token = _token.asStateFlow()

    private val _userInfo = MutableSharedFlow<UserInformation>()
    val userInfo = _userInfo.asSharedFlow()


    fun getNames() {
        viewModelScope.launch {
            getNameUseCase.getName().onEach {
                _name.emit(it)
                checkInfoAndEmit()
            }.launchIn(viewModelScope)

            getTokenUseCase.getToken().onEach {
                _token.emit(it)
                checkInfoAndEmit()
            }.launchIn(viewModelScope)

            getUserNameUseCase.getUserName().onEach {
                _userName.emit(it)
                checkInfoAndEmit()
            }.launchIn(viewModelScope)
        }
    }

    private fun checkInfoAndEmit() {
        if (userName.value.isNotEmpty() && token.value.isNotEmpty() && name.value.isNotEmpty())
            viewModelScope.launch {
                _userInfo.emit(
                    UserInformation(
                        userName = userName.value,
                        name = name.value,
                        token = token.value
                    )
                )
            }
    }
}