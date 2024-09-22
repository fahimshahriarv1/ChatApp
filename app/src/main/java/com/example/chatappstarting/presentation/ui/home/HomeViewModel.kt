package com.example.chatappstarting.presentation.ui.home

import androidx.lifecycle.viewModelScope
import com.example.chatappstarting.data.firebase.FireBaseClient
import com.example.chatappstarting.data.room.model.UserInfo
import com.example.chatappstarting.data.room.model.UserInformation
import com.example.chatappstarting.domain.usecases.GetConnectedUsersUseCase
import com.example.chatappstarting.presentation.ui.common.CommonViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val client: FireBaseClient,
    getConnectedUsersUseCase: GetConnectedUsersUseCase
) : CommonViewModel() {

    private val _userList = MutableSharedFlow<List<UserInformation>>()
    val userList = _userList.asSharedFlow()

    private var connectionList = listOf<String>()

    init {
        getConnectedUsersUseCase.getConnectionList().onEach {
            connectionList = it
            if (it.isNotEmpty())
                addStatusObserver()
        }.launchIn(viewModelScope)
    }

    private fun addStatusObserver() {
        client.observeUserStatus { list ->
            viewModelScope.launch {
                _userList.emit(list?.filter { user -> user.userName in connectionList } ?: emptyList())
            }
        }
    }
}
