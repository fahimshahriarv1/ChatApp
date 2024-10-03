package com.example.chatappstarting.presentation.ui.home

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.chatappstarting.data.room.model.UserInformation
import com.example.chatappstarting.domain.usecases.GetConnectedUsersUseCase
import com.example.chatappstarting.domain.usecases.SaveConnectedUsersUseCase
import com.example.chatappstarting.presentation.ui.common.CommonViewModel
import com.example.chatappstarting.presentation.ui.home.model.StatusEnum
import com.example.chatappstarting.service.ServiceRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getConnectedUsersUseCase: GetConnectedUsersUseCase,
    private val serviceRepo: ServiceRepo,
    private val saveConnectedUsersUseCase: SaveConnectedUsersUseCase
) : CommonViewModel() {

    private val _userList = MutableSharedFlow<List<UserInformation>>()
    val userList = _userList.asSharedFlow()

    private var connectionList = mutableListOf<String>()

    lateinit var userInformation: UserInformation

    val userAddText = mutableStateOf("")

    init {
        loaderState.value = true

        userInfo.onEach {
            Log.d("info emitted", it.toString())
            userInformation = it
            if (it.userName.isNotEmpty()) {
                getConnectedUsersUseCase.getConnectionList(it.userName).onEach { list ->
                    Log.d("operation", "getiing list + $list")
                    if (list.isNotEmpty() && connectionList.isEmpty()) {
                        connectionList =
                            list.first()
                                .removePrefix("[")
                                .removeSuffix("]").split(",")
                                .map { name ->
                                    name.removePrefix("\"")
                                        .removeSuffix("\"")
                                }
                                .toMutableList()
                        addStatusObserver()
                    }
                }.launchIn(viewModelScope)
                startListeners()
            }
        }.launchIn(viewModelScope)
    }

    private fun addStatusObserver() {
        fireBaseClient.observeUserStatus { list ->
            viewModelScope.launch {
                _userList.emit(list?.filter { user -> user.userName in connectionList && user.userName.isNotEmpty() }
                    ?: emptyList())

                loaderState.value = false
            }
        }
    }

    fun startListeners() {
        if (::userInformation.isInitialized) {
            Log.d("listener", "started")
            addStatusObserver()
            fireBaseClient.setStatus(userInformation.userName, StatusEnum.ONLINE)
            fireBaseClient.observeLoginStatus(
                userInformation.userName,
                userInformation.token
            ) { isMatched ->
                if (!isMatched)
                    logout()
            }
        }
    }

    fun onAdduserClicked() {
        loaderState.value = true
        when (userAddText.value) {
            userName.value -> {
                showToast("You cannot add yourself as connection")
                loaderState.value = false
                userAddText.value = ""
            }

            !in connectionList -> fireBaseClient.checkUserExistence(
                userAddText.value,
                existOrNot = {
                    if (it) {
                        connectionList.add(userAddText.value)
                        fireBaseClient.addConnection(
                            userName.value,
                            connectionList,
                            onSuccess = {
                                viewModelScope.launch {
                                    saveConnectedUsersUseCase.saveConnectedList(
                                        userInformation.userName,
                                        connectionList
                                    ).collect { result ->
                                        result.onSuccess {
                                            showToast("Connection Added Successfully")
                                            loaderState.value = false
                                        }
                                        result.onFailure {
                                            showToast("Something went wrong")
                                            loaderState.value = false
                                        }
                                    }
                                }
                            },
                            onFailure = {
                                showToast("Something went wrong")
                                loaderState.value = false
                            }
                        )
                    } else {
                        loaderState.value = false
                        showToast("User doesn't have an account")
                    }
                },
                onFailed = {
                    loaderState.value = false
                    showToast("Something went wrong")
                }
            )

            else -> {
                loaderState.value = false
                showToast("Connection Already Exist")
            }
        }
    }

    fun onLogout(onSuccess: () -> Unit = {}) {
        fireBaseClient.setStatus(userName.value, StatusEnum.OFFLINE)
        logout(onSuccess)
    }
}
