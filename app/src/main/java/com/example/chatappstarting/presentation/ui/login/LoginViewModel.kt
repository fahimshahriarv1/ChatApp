package com.example.chatappstarting.presentation.ui.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.chatappstarting.data.firebase.FireBaseClient
import com.example.chatappstarting.data.room.model.UserInformation
import com.example.chatappstarting.domain.usecases.SaveConnectedUsersUseCase
import com.example.chatappstarting.domain.usecases.SaveMobileUseCase
import com.example.chatappstarting.domain.usecases.SaveNameUseCase
import com.example.chatappstarting.domain.usecases.SaveTokenUseCase
import com.example.chatappstarting.domain.usecases.SaveUserNameUseCase
import com.example.chatappstarting.presentation.navgraph.Route
import com.example.chatappstarting.presentation.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val fireBaseClient: FireBaseClient,
    private val saveMobileUseCase: SaveMobileUseCase,
    private val saveTokenUseCase: SaveTokenUseCase,
    private val saveConnectedUsersUseCase: SaveConnectedUsersUseCase,
    private val setUserNameUseCase: SaveUserNameUseCase,
    private val setNameUseCase: SaveNameUseCase
) :
    BaseViewModel() {
    val uname = mutableStateOf("")
    val pass = mutableStateOf("")
    val isError = mutableStateOf(false)

    fun onUnameChanged(name: String) {
        uname.value = name
    }

    fun onPassChanged(pwd: String) {
        pass.value = pwd
    }

    fun onLoginClicked() {
        loaderState.value = true
        fireBaseClient.login(uname.value, ::checkPwd, ::onUserNotFound)
    }

    fun onSignUpClicked() {
        navigateTo(Route.AppSignUp.route)
    }

    private fun checkPwd(user: UserInformation) {
        if (user.password == pass.value) {
            viewModelScope.launch {
                saveTokenUseCase.saveToken(user.token)
                saveMobileUseCase.saveMobileNumber(uname.value)
                saveConnectedUsersUseCase.saveConnectedList(user.usersConnected)
                setNameUseCase.saveName(user.name)
                setUserNameUseCase.saveUserName(user.userName)
                navigateTo(Route.HomeScreen.route, isSingleTop = true)
                loaderState.value = false
            }
        } else {
            loaderState.value = false
            showToast("Unam or pass invalid")
        }
    }

    private fun onUserNotFound() {
        loaderState.value = false
        showToast("no user found")
    }
}