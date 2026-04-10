package com.fahimshahriarv1.mtom.data.crypto

import android.util.Base64
import android.util.Log
import com.fahimshahriarv1.mtom.BuildConfig
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CryptoManager @Inject constructor() {

    companion object {
        private const val TAG = "CryptoManager"
        private const val AES_GCM = "AES/GCM/NoPadding"
        private const val GCM_IV_LENGTH = 12
        private const val GCM_TAG_LENGTH = 128
    }

    // Cache derived keys per chatId
    private val keyCache = mutableMapOf<String, SecretKey>()

    /**
     * Derives a unique AES-256 key for a conversation.
     * Uses both usernames + the ASCII sum of their chars as a constant.
     * Both sides derive the same key independently — nothing stored on server.
     */
    fun deriveConversationKey(user1: String, user2: String): SecretKey {
        // Sort so both sides produce the same key regardless of who is sender/receiver
        val sorted = listOf(user1, user2).sorted()
        val chatId = "${sorted[0]}_${sorted[1]}"

        keyCache[chatId]?.let { return it }

        val asciiSum = (sorted[0] + sorted[1]).sumOf { it.code }
        val raw = "${sorted[0]}:${sorted[1]}:$asciiSum:${BuildConfig.E2EE_SECRET}"

        // SHA-256 to get a 32-byte AES key
        val digest = MessageDigest.getInstance("SHA-256")
        val keyBytes = digest.digest(raw.toByteArray(Charsets.UTF_8))

        val secretKey = SecretKeySpec(keyBytes, "AES")
        keyCache[chatId] = secretKey
        Log.d(TAG, "Derived key for conversation $chatId")
        return secretKey
    }

    /**
     * Encrypts plaintext. Returns Base64(IV + ciphertext).
     */
    fun encrypt(plaintext: String, key: SecretKey): String {
        val cipher = Cipher.getInstance(AES_GCM)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val iv = cipher.iv
        val ciphertext = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))

        val combined = ByteArray(iv.size + ciphertext.size)
        System.arraycopy(iv, 0, combined, 0, iv.size)
        System.arraycopy(ciphertext, 0, combined, iv.size, ciphertext.size)

        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    /**
     * Decrypts Base64(IV + ciphertext) back to plaintext.
     */
    fun decrypt(encryptedB64: String, key: SecretKey): String {
        val combined = Base64.decode(encryptedB64, Base64.NO_WRAP)
        val iv = combined.copyOfRange(0, GCM_IV_LENGTH)
        val ciphertext = combined.copyOfRange(GCM_IV_LENGTH, combined.size)

        val cipher = Cipher.getInstance(AES_GCM)
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(GCM_TAG_LENGTH, iv))
        val plainBytes = cipher.doFinal(ciphertext)

        return String(plainBytes, Charsets.UTF_8)
    }
}
