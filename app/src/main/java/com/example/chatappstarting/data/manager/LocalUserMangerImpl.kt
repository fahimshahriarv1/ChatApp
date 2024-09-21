package com.example.chatappstarting.data.manager

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.chatappstarting.constants.IS_LOGGED_IN
import com.example.chatappstarting.constants.LOCAL_USER_MANAGER
import com.example.chatappstarting.constants.MOBILE_NUMBER
import com.example.chatappstarting.constants.NAME
import com.example.chatappstarting.constants.REFRESH_TOKEN
import com.example.chatappstarting.constants.TOKEN
import com.example.chatappstarting.constants.USER_LIST
import com.example.chatappstarting.constants.USER_NAME
import com.example.chatappstarting.data.room.model.UserInformation
import com.example.chatappstarting.domain.manager.LocalUserManger
import com.example.chatappstarting.presentation.utils.emptyIfNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalUserMangerImpl(private val context: Context) : LocalUserManger {
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

    override suspend fun saveConnectedList(list: List<String>) {
        context.dataStore.edit { localUserInfo ->
            localUserInfo[PreferencesKeys.users] =
                if (list.size > 1) list.joinToString { "," } else list.first()
        }
    }

    override fun getConnectedList(): Flow<List<String>> {
        return context.dataStore.data.map { localUserInfo ->
            if (localUserInfo[PreferencesKeys.users]?.contains(",") == true)
                localUserInfo[PreferencesKeys.users]?.split(",") ?: emptyList()
            else
                listOf(localUserInfo[PreferencesKeys.users] ?: "")
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
                usersConnected = if (it[PreferencesKeys.users]?.contains(",") == true)
                    it[PreferencesKeys.users]?.split(",") ?: emptyList()
                else
                    listOf(it[PreferencesKeys.users] ?: "")
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