package com.example.chatappstarting.presentation.ui.common

import androidx.lifecycle.viewModelScope
import com.example.chatappstarting.domain.usecases.GetNameUseCase
import com.example.chatappstarting.domain.usecases.GetUserNameUseCase
import com.example.chatappstarting.presentation.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    private val _userName = MutableSharedFlow<String>()
    val userName = _userName.asSharedFlow()

    private val _name = MutableSharedFlow<String>()
    val name = _name.asSharedFlow()


    fun getNames() {
        viewModelScope.launch {
            getNameUseCase.getName().onEach {
                _name.emit(it)
            }.launchIn(viewModelScope)

            getUserNameUseCase.getUserName().onEach {
                _userName.emit(it)
            }.launchIn(viewModelScope)
        }
    }
}