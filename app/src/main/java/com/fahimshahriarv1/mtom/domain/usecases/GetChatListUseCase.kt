package com.fahimshahriarv1.mtom.domain.usecases

import com.fahimshahriarv1.mtom.data.room.model.ChatUserEntity
import com.fahimshahriarv1.mtom.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChatListUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    fun getChatList(): Flow<List<ChatUserEntity>> {
        return chatRepository.getChatList()
    }
}
