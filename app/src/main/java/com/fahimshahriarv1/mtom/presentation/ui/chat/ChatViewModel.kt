package com.fahimshahriarv1.mtom.presentation.ui.chat

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.fahimshahriarv1.mtom.data.room.model.MessageInfoEntity
import com.fahimshahriarv1.mtom.domain.repository.LocalUserRepository
import com.fahimshahriarv1.mtom.domain.repository.ChatRepository
import com.fahimshahriarv1.mtom.domain.usecases.GetChatMessagesUseCase
import com.fahimshahriarv1.mtom.domain.usecases.SendMessageUseCase
import com.fahimshahriarv1.mtom.presentation.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val sendMessageUseCase: SendMessageUseCase,
    private val getChatMessagesUseCase: GetChatMessagesUseCase,
    private val chatRepository: ChatRepository,
    private val localUserRepository: LocalUserRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    val chatId: String = savedStateHandle.get<String>("chat_id") ?: ""
    val recipientName: String = savedStateHandle.get<String>("recipient_name") ?: ""

    private val _messageInput = mutableStateOf("")
    val messageInput: State<String> = _messageInput

    private val _messages = MutableStateFlow<List<MessageInfoEntity>>(emptyList())
    val messages: StateFlow<List<MessageInfoEntity>> = _messages.asStateFlow()

    private val _currentUserId = MutableStateFlow("")
    val currentUserId: StateFlow<String> = _currentUserId.asStateFlow()

    private val _isRecipientOnline = MutableStateFlow(false)
    val isRecipientOnline: StateFlow<Boolean> = _isRecipientOnline.asStateFlow()

    init {
        observeMessages()
        loadCurrentUser()
        markChatAsRead()
    }

    private fun markChatAsRead() {
        if (chatId.isNotEmpty()) {
            viewModelScope.launch {
                chatRepository.resetUnreadCount(chatId)
            }
        }
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            localUserRepository.getUserInfo().onEach {
                if (it.userName.isNotEmpty()) {
                    _currentUserId.value = it.userName
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun observeMessages() {
        getChatMessagesUseCase.getMessages(chatId)
            .onEach { _messages.value = it }
            .launchIn(viewModelScope)
    }

    private var statusObserverStarted = false

    fun startStatusObserver() {
        if (statusObserverStarted || recipientName.isEmpty()) return
        statusObserverStarted = true
        fireBaseClient.observeUserStatus { list ->
            val isOnline = list?.any { it.userName == recipientName && it.status == com.fahimshahriarv1.mtom.domain.model.StatusEnum.ONLINE } == true
            _isRecipientOnline.value = isOnline
        }
    }

    fun onMessageInputChanged(input: String) {
        _messageInput.value = input
    }

    fun sendMessage() {
        val text = _messageInput.value.trim()
        val userId = _currentUserId.value
        if (text.isEmpty() || userId.isEmpty()) return

        viewModelScope.launch {
            try {
                sendMessageUseCase.sendMessage(
                    chatId = chatId,
                    recipientId = recipientName,
                    senderId = userId,
                    message = text
                )
                _messageInput.value = ""
            } catch (e: Exception) {
                Log.e("ChatVM", "Send failed: ${e.message}")
                showToast("Send failed: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}
