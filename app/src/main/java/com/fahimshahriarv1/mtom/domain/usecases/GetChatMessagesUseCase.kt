package com.fahimshahriarv1.mtom.domain.usecases

import com.fahimshahriarv1.mtom.data.room.model.MessageInfoEntity
import com.fahimshahriarv1.mtom.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChatMessagesUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    fun getMessages(chatId: String): Flow<List<MessageInfoEntity>> {
        return chatRepository.getMessagesForChat(chatId)
    }
}
