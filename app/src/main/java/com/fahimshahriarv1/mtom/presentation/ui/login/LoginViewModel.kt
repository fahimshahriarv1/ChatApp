package com.fahimshahriarv1.mtom.presentation.ui.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.fahimshahriarv1.mtom.data.room.model.UserInformation
import com.fahimshahriarv1.mtom.domain.usecases.SaveConnectedUsersUseCase
import com.fahimshahriarv1.mtom.domain.usecases.SaveMobileUseCase
import com.fahimshahriarv1.mtom.domain.usecases.SaveNameUseCase
import com.fahimshahriarv1.mtom.domain.usecases.SaveTokenUseCase
import com.fahimshahriarv1.mtom.domain.usecases.SaveUserNameUseCase
import com.fahimshahriarv1.mtom.presentation.navgraph.Route
import com.fahimshahriarv1.mtom.presentation.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
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

    fun onLoginClicked(onSuccess: () -> Unit = {}) {
        loaderState.value = true
        fireBaseClient.login(uname.value, ::checkPwd, ::onUserNotFound,onSuccess)
    }

    fun onSignUpClicked() {
        navigateTo(Route.AppSignUp.route)
    }

    private fun checkPwd(user: UserInformation, onSuccess: () -> Unit = {}) {
        if (user.password == pass.value) {
            viewModelScope.launch {
                saveTokenUseCase.saveToken(user.token)
                saveMobileUseCase.saveMobileNumber(uname.value)
                delay(500)
                setNameUseCase.saveName(user.name)
                setUserNameUseCase.saveUserName(user.userName)
                saveConnectedUsersUseCase.saveConnectedList(user.userName, user.usersConnected)
                    .collect {
                        it.onSuccess {
                            onSuccess()
                            loaderState.value = false
                        }
                        it.onFailure {
                            showToast("Something went wrong")
                            loaderState.value = false
                        }
                    }
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