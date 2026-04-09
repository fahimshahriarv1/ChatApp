package com.fahimshahriarv1.mtom.presentation.ui.home

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.fahimshahriarv1.mtom.data.room.model.ChatUserEntity
import com.fahimshahriarv1.mtom.data.room.model.UserInformation
import com.fahimshahriarv1.mtom.domain.usecases.GetChatListUseCase
import com.fahimshahriarv1.mtom.domain.usecases.GetConnectedUsersUseCase
import com.fahimshahriarv1.mtom.domain.usecases.SaveConnectedUsersUseCase
import com.fahimshahriarv1.mtom.presentation.ui.common.CommonViewModel
import com.fahimshahriarv1.mtom.domain.model.StatusEnum
import com.fahimshahriarv1.mtom.service.ServiceRepo
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
    private val saveConnectedUsersUseCase: SaveConnectedUsersUseCase,
    getChatListUseCase: GetChatListUseCase
) : CommonViewModel() {

    private val _userList = MutableSharedFlow<List<UserInformation>>()
    val userList = _userList.asSharedFlow()

    val chatList = getChatListUseCase.getChatList()

    private var connectionList = mutableListOf<String>()

    lateinit var userInformation: UserInformation

    val userAddText = mutableStateOf("")

    init {
        loaderState.value = true

        userInfo.onEach {
            Log.d("info emitted", it.toString())
            userInformation = it
            if (it.userName.isNotEmpty()) {
                serviceRepo.startService(it.userName)
                syncConnectionsFromFirestore(it.userName)

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

    private fun syncConnectionsFromFirestore(userName: String) {
        fireBaseClient.fetchConnectionList(userName) { remoteList ->
            if (remoteList.isNotEmpty()) {
                // Merge remote into local
                val merged = (connectionList + remoteList).distinct().filter { it.isNotEmpty() }.toMutableList()
                if (merged != connectionList) {
                    connectionList = merged
                    viewModelScope.launch {
                        saveConnectedUsersUseCase.saveConnectedList(userName, connectionList)
                            .collect { /* synced */ }
                        addStatusObserver()
                    }
                    Log.d("HomeVM", "Synced connections from Firestore: $connectionList")
                }
            }
        }
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
        if (::userInformation.isInitialized && userInformation.userName.isNotEmpty()) {
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

    private val _navigateToChat = MutableSharedFlow<Pair<String, String>>()
    val navigateToChat = _navigateToChat.asSharedFlow()

    fun onAdduserClicked() {
        val currentUserName = if (::userInformation.isInitialized) userInformation.userName else userName.value
        if (currentUserName.isEmpty()) {
            showToast("Please wait, loading...")
            return
        }
        val recipientName = userAddText.value.trim()
        if (recipientName.isEmpty()) {
            showToast("Enter a number")
            return
        }
        if (recipientName == currentUserName) {
            showToast("You cannot chat with yourself")
            userAddText.value = ""
            return
        }
        loaderState.value = true
        fireBaseClient.checkUserExistence(
            recipientName,
            existOrNot = { exists ->
                if (exists) {
                    // Add mutual connection: A adds B, B auto-adds A
                    fireBaseClient.addMutualConnection(currentUserName, recipientName) {
                        // Save to local Room DB
                        if (recipientName !in connectionList) {
                            connectionList.add(recipientName)
                        }
                        viewModelScope.launch {
                            saveConnectedUsersUseCase.saveConnectedList(
                                currentUserName, connectionList
                            ).collect { /* saved */ }
                            addStatusObserver()

                            val chatId = if (currentUserName < recipientName)
                                "${currentUserName}_$recipientName"
                            else
                                "${recipientName}_$currentUserName"
                            loaderState.value = false
                            userAddText.value = ""
                            _navigateToChat.emit(chatId to recipientName)
                        }
                    }
                } else {
                    loaderState.value = false
                    showToast("User doesn't have an account")
                }
            },
            onFailed = { e ->
                loaderState.value = false
                showToast("Error: ${e.message}")
            }
        )
    }

    fun onLogout(onSuccess: () -> Unit = {}) {
        fireBaseClient.setStatus(userName.value, StatusEnum.OFFLINE)
        logout(onSuccess)
    }
}
