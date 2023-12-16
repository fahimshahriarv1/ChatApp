package com.example.chatappstarting.data.manager

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.chatappstarting.constants.IS_LOGGED_IN
import com.example.chatappstarting.constants.LOCAL_USER_MANaGER
import com.example.chatappstarting.constants.REFRESH_TOKEN
import com.example.chatappstarting.constants.TOKEN
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

    override fun getUserToken(): Flow<String> {
        return context.dataStore.data.map {
            it[PreferencesKeys.token].emptyIfNull()
        }
    }

    override fun getUserLoggedInState(): Flow<Boolean> {
        return context.dataStore.data.map {
            it[PreferencesKeys.token]?.isNotEmpty() == true
        }
    }
}

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = LOCAL_USER_MANaGER)

private object PreferencesKeys {
    val isLoggedIn = booleanPreferencesKey(name = IS_LOGGED_IN)
    val token = stringPreferencesKey(name = TOKEN)
    val refreshToken = stringPreferencesKey(name = REFRESH_TOKEN)
}