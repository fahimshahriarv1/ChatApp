package com.fahimshahriarv1.mtom.domain.usecases

import com.fahimshahriarv1.mtom.data.room.LocalDatabase
import com.fahimshahriarv1.mtom.domain.repository.LocalUserRepository
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val localUserRepository: LocalUserRepository,
    private val db: LocalDatabase
) {
    suspend fun logout() = useCaseHandler(Dispatchers.IO) {
        localUserRepository.clearPreferences()
        db.getMessageInfo().deleteAllChat()
        db.getUserList().deleteAllChat()
        Result.success(Unit)
    }
}
