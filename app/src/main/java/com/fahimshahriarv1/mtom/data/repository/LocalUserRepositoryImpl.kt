package com.fahimshahriarv1.mtom.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.fahimshahriarv1.mtom.constants.IS_LOGGED_IN
import com.fahimshahriarv1.mtom.constants.LOCAL_USER_MANAGER
import com.fahimshahriarv1.mtom.constants.MOBILE_NUMBER
import com.fahimshahriarv1.mtom.constants.NAME
import com.fahimshahriarv1.mtom.constants.REFRESH_TOKEN
import com.fahimshahriarv1.mtom.constants.TOKEN
import com.fahimshahriarv1.mtom.constants.USER_LIST
import com.fahimshahriarv1.mtom.constants.USER_NAME
import com.fahimshahriarv1.mtom.data.room.model.UserInformation
import com.fahimshahriarv1.mtom.domain.repository.LocalUserRepository
import com.fahimshahriarv1.mtom.presentation.utils.emptyIfNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalUserRepositoryImpl(private val context: Context) : LocalUserRepository {
    override suspend fun saveUserToken(token: String) {
        context.dataStore.edit { localUserInfo ->
            localUserInfo[PreferencesKeys.isLoggedIn] = true
            localUserInfo[PreferencesKeys.token] = token
        }
    }

    override suspend fun removeLoggedInState() {
        context.dataStore.edit { localUserInfo ->
            localUserInfo[PreferencesKeys.isLoggedIn] = false
            localUserInfo[PreferencesKeys.token] = ""
        }
    }

    override suspend fun saveMobileNumber(number: String) {
        context.dataStore.edit { localUserInfo ->
            localUserInfo[PreferencesKeys.mobileNumber] = number
        }
    }

    override suspend fun clearPreferences() {
        context.dataStore.edit {
            it.clear()
        }
    }

    override suspend fun getUserName(): Flow<String> {
        return context.dataStore.data.map {
            it[PreferencesKeys.userName].emptyIfNull()
        }
    }

    override suspend fun getUserNameOnly(): Flow<String> {
        return context.dataStore.data.map {
            it[PreferencesKeys.name].emptyIfNull()
        }
    }

    override suspend fun setUserName(uName: String) {
        context.dataStore.edit { localUserInfo ->
            localUserInfo[PreferencesKeys.userName] = uName
        }
    }

    override suspend fun setUserNameOnly(name: String) {
        context.dataStore.edit { localUserInfo ->
            localUserInfo[PreferencesKeys.name] = name
        }
    }

    override fun getUserToken(): Flow<String> {
        return context.dataStore.data.map {
            it[PreferencesKeys.token].emptyIfNull()
        }
    }

    override fun getUserInfo(): Flow<UserInformation> {
        return context.dataStore.data.map {
            UserInformation(
                token = it[PreferencesKeys.token].emptyIfNull(),
                password = "",
                userName = it[PreferencesKeys.userName].emptyIfNull(),
                name = it[PreferencesKeys.name].emptyIfNull(),
                usersConnected = listOf()
            )
        }
    }

    override fun getUserLoggedInState(): Flow<Boolean> {
        return context.dataStore.data.map {
            it[PreferencesKeys.token]?.isNotEmpty() == true
        }
    }

}

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = LOCAL_USER_MANAGER)

private object PreferencesKeys {
    val isLoggedIn = booleanPreferencesKey(name = IS_LOGGED_IN)
    val token = stringPreferencesKey(name = TOKEN)
    val refreshToken = stringPreferencesKey(name = REFRESH_TOKEN)
    val mobileNumber = stringPreferencesKey(name = MOBILE_NUMBER)
    val users = stringPreferencesKey(name = USER_LIST)
    val userName = stringPreferencesKey(name = USER_NAME)
    val name = stringPreferencesKey(name = NAME)
}