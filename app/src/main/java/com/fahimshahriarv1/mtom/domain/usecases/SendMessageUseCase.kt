package com.fahimshahriarv1.mtom.domain.usecases

import com.fahimshahriarv1.mtom.domain.repository.ChatRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend fun sendMessage(chatId: String, recipientId: String, senderId: String, message: String) {
        chatRepository.sendMessage(chatId, recipientId, senderId, message)
    }
}
