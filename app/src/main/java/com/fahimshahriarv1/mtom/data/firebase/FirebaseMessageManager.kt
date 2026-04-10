package com.fahimshahriarv1.mtom.data.firebase

import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseMessageManager @Inject constructor() {

    companion object {
        private const val TAG = "FirebaseMsg"
        private const val MESSAGES_PATH = "messages"
        private const val REGISTERED_USERS_PATH = "registered_users"
    }

    private val database = FirebaseDatabase.getInstance()
    private var listener: ChildEventListener? = null
    private var listeningUserId: String? = null
    val currentUser: String? get() = listeningUserId

    init {
        Log.d(TAG, "FirebaseMessageManager init, DB URL: ${database.reference}")
        // Quick write test to verify RTDB is reachable
        database.getReference("_test_ping").setValue(System.currentTimeMillis())
            .addOnSuccessListener { Log.d(TAG, "RTDB WRITE TEST: SUCCESS — database is reachable") }
            .addOnFailureListener { Log.e(TAG, "RTDB WRITE TEST: FAILED — ${it.message}") }
    }

    fun registerUser(userId: String, onComplete: () -> Unit = {}) {
        database.getReference(REGISTERED_USERS_PATH).child(userId).setValue(true)
            .addOnSuccessListener {
                Log.d(TAG, "User $userId registered in RTDB")
                onComplete()
            }
            .addOnFailureListener {
                Log.e(TAG, "Failed to register user in RTDB: ${it.message}")
                onComplete()
            }
    }

    fun sendMessage(recipientId: String, senderId: String, message: String, onResult: (Result<Unit>) -> Unit) {
        val msgData = mapOf(
            "senderId" to senderId,
            "message" to message,
            "timestamp" to System.currentTimeMillis()
        )

        val ref = database.getReference(MESSAGES_PATH).child(recipientId).push()
        Log.d(TAG, "Sending to path: $MESSAGES_PATH/$recipientId, key=${ref.key}")

        ref.setValue(msgData)
            .addOnSuccessListener {
                Log.d(TAG, "SEND OK to $recipientId: $message")
                onResult(Result.success(Unit))
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "SEND FAILED to $recipientId: ${e.message}")
                e.printStackTrace()
                onResult(Result.failure(e))
            }
    }

    fun startListening(
        userId: String,
        onMessageReceived: (senderId: String, message: String, timestamp: Long) -> Unit
    ) {
        // Don't double-listen
        if (listeningUserId == userId && listener != null) {
            Log.d(TAG, "Already listening for $userId, skipping")
            return
        }

        stopListening()
        listeningUserId = userId

        val ref = database.getReference(MESSAGES_PATH).child(userId)
        Log.d(TAG, "Setting up listener at path: $MESSAGES_PATH/$userId")

        // Quick connectivity check
        database.getReference(".info/connected").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                Log.d(TAG, "Firebase RTDB connected: $connected")
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Connectivity check failed: ${error.message}")
            }
        })

        listener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, ">>> onChildAdded: key=${snapshot.key}, value=${snapshot.value}")
                val senderId = snapshot.child("senderId").getValue(String::class.java)
                val message = snapshot.child("message").getValue(String::class.java)
                val timestamp = snapshot.child("timestamp").getValue(Long::class.java) ?: System.currentTimeMillis()

                if (senderId == null || message == null) {
                    Log.e(TAG, "Invalid message data: senderId=$senderId, message=$message")
                    snapshot.ref.removeValue()
                    return
                }

                Log.d(TAG, ">>> RECEIVED from $senderId: $message")
                onMessageReceived(senderId, message, timestamp)

                // Delete after processing — Firebase is just a relay
                snapshot.ref.removeValue()
                    .addOnSuccessListener { Log.d(TAG, "Deleted processed message ${snapshot.key}") }
                    .addOnFailureListener { Log.e(TAG, "Failed to delete message: ${it.message}") }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "LISTENER CANCELLED: ${error.message}, code=${error.code}")
            }
        }

        ref.addChildEventListener(listener!!)
        Log.d(TAG, "Listening for messages for $userId at $MESSAGES_PATH/$userId")
    }

    fun stopListening() {
        val userId = listeningUserId ?: return
        val l = listener ?: return

        database.getReference(MESSAGES_PATH).child(userId).removeEventListener(l)
        listener = null
        listeningUserId = null
        Log.d(TAG, "Stopped listening for $userId")
    }
}
