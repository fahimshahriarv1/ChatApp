package com.fahimshahriarv1.mtom.data.backup

import android.content.Context
import android.net.Uri
import com.fahimshahriarv1.mtom.data.room.LocalDatabase
import com.google.api.client.http.ByteArrayContent
import com.google.api.services.drive.Drive
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatBackupManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val db: LocalDatabase
) {
    private val gson = Gson()

    private suspend fun createBackupData(): BackupData {
        val messages = db.getMessageInfo().getAllMessages()
        val chatUsers = db.getUserList().getAllChatUsers()
        val connectedUsers = db.getUserConnectionList().getAllConnections()

        return BackupData(
            version = 1,
            timestamp = System.currentTimeMillis(),
            messages = messages,
            chatUsers = chatUsers,
            connectedUsers = connectedUsers
        )
    }

    suspend fun backupToUri(uri: Uri): Long = withContext(Dispatchers.IO) {
        val data = createBackupData()
        val json = gson.toJson(data)
        context.contentResolver.openOutputStream(uri)?.use {
            it.write(json.toByteArray())
        } ?: throw Exception("Could not write to selected location")
        data.timestamp
    }

    suspend fun restoreFromUri(uri: Uri) = withContext(Dispatchers.IO) {
        val json = context.contentResolver.openInputStream(uri)?.use {
            it.bufferedReader().readText()
        } ?: throw Exception("Could not read backup file")

        val data = gson.fromJson(json, BackupData::class.java)
            ?: throw Exception("Invalid backup file")

        db.getMessageInfo().deleteAllChat()
        db.getUserList().deleteAllChat()

        data.messages.forEach { db.getMessageInfo().insertNewMessage(it) }
        data.chatUsers.forEach { db.getUserList().insertNewChatUser(it) }
        data.connectedUsers.forEach { db.getUserConnectionList().saveConnectionList(it) }
    }

    suspend fun backupToDrive(driveService: Drive): Long = withContext(Dispatchers.IO) {
        val data = createBackupData()
        val json = gson.toJson(data)

        // Delete old backup if exists
        val existingFiles = driveService.files().list()
            .setSpaces("appDataFolder")
            .setQ("name = 'mtom_chat_backup.json'")
            .execute()

        existingFiles.files?.forEach {
            driveService.files().delete(it.id).execute()
        }

        // Upload new backup
        val metadata = com.google.api.services.drive.model.File()
            .setName("mtom_chat_backup.json")
            .setParents(listOf("appDataFolder"))
        val content = ByteArrayContent.fromString("application/json", json)
        driveService.files().create(metadata, content).setFields("id").execute()

        data.timestamp
    }

    suspend fun restoreFromDrive(driveService: Drive) = withContext(Dispatchers.IO) {
        val files = driveService.files().list()
            .setSpaces("appDataFolder")
            .setQ("name = 'mtom_chat_backup.json'")
            .execute()

        val file = files.files?.firstOrNull()
            ?: throw Exception("No backup found on Google Drive")

        val stream = ByteArrayOutputStream()
        driveService.files().get(file.id).executeMediaAndDownloadTo(stream)
        val json = stream.toString("UTF-8")

        val data = gson.fromJson(json, BackupData::class.java)
            ?: throw Exception("Invalid backup data from Drive")

        db.getMessageInfo().deleteAllChat()
        db.getUserList().deleteAllChat()

        data.messages.forEach { db.getMessageInfo().insertNewMessage(it) }
        data.chatUsers.forEach { db.getUserList().insertNewChatUser(it) }
        data.connectedUsers.forEach { db.getUserConnectionList().saveConnectionList(it) }
    }
}
