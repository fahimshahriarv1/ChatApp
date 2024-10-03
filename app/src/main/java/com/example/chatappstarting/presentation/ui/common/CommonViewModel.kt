package com.example.chatappstarting.presentation.ui.common

import androidx.lifecycle.viewModelScope
import com.example.chatappstarting.data.room.model.UserInformation
import com.example.chatappstarting.domain.usecases.GetUserInfoUseCase
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
    lateinit var getUserInfoUseCase: GetUserInfoUseCase

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
            getUserInfoUseCase.getUserInfo().onEach {
                _userInfo.emit(it)
                _userName.emit(it.userName)
                _name.emit(it.name)
                _token.emit(it.token)
            }.launchIn(viewModelScope)
        }
    }
}