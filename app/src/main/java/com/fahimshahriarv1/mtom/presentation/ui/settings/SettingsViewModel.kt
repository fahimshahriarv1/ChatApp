package com.fahimshahriarv1.mtom.presentation.ui.settings

import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.fahimshahriarv1.mtom.data.backup.ChatBackupManager
import com.fahimshahriarv1.mtom.domain.model.StatusEnum
import com.fahimshahriarv1.mtom.presentation.ui.common.CommonViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val chatBackupManager: ChatBackupManager
) : CommonViewModel() {

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing = _isProcessing.asStateFlow()

    private val _lastLocalBackup = MutableStateFlow<Long?>(null)
    val lastLocalBackup = _lastLocalBackup.asStateFlow()

    private val _lastDriveBackup = MutableStateFlow<Long?>(null)
    val lastDriveBackup = _lastDriveBackup.asStateFlow()

    fun backupToLocal(uri: Uri) {
        viewModelScope.launch {
            _isProcessing.value = true
            try {
                val timestamp = chatBackupManager.backupToUri(uri)
                _lastLocalBackup.value = timestamp
                showToast("Chat backup saved successfully")
            } catch (e: Exception) {
                showToast("Backup failed: ${e.message}")
            }
            _isProcessing.value = false
        }
    }

    fun restoreFromLocal(uri: Uri) {
        viewModelScope.launch {
            _isProcessing.value = true
            try {
                chatBackupManager.restoreFromUri(uri)
                showToast("Chats restored successfully")
            } catch (e: Exception) {
                showToast("Restore failed: ${e.message}")
            }
            _isProcessing.value = false
        }
    }

    fun backupToDrive(account: GoogleSignInAccount) {
        viewModelScope.launch {
            _isProcessing.value = true
            try {
                val driveService = buildDriveService(account)
                val timestamp = chatBackupManager.backupToDrive(driveService)
                _lastDriveBackup.value = timestamp
                showToast("Backup to Google Drive successful")
            } catch (e: Exception) {
                showToast("Drive backup failed: ${e.message}")
            }
            _isProcessing.value = false
        }
    }

    fun restoreFromDrive(account: GoogleSignInAccount) {
        viewModelScope.launch {
            _isProcessing.value = true
            try {
                val driveService = buildDriveService(account)
                chatBackupManager.restoreFromDrive(driveService)
                showToast("Restored from Google Drive successfully")
            } catch (e: Exception) {
                showToast("Drive restore failed: ${e.message}")
            }
            _isProcessing.value = false
        }
    }

    private fun buildDriveService(account: GoogleSignInAccount): Drive {
        val credential = GoogleAccountCredential.usingOAuth2(
            appContext, listOf(DriveScopes.DRIVE_APPDATA)
        )
        credential.selectedAccount = account.account
        return Drive.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        ).setApplicationName("MtoM").build()
    }

    fun onLogout(onSuccess: () -> Unit) {
        fireBaseClient.setStatus(userName.value, StatusEnum.OFFLINE)
        logout(onSuccess)
    }
}
