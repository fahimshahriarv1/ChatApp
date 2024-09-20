package com.example.chatappstarting.data.firebase

import android.util.Log
import com.example.chatappstarting.constants.LOGIN_TOKEN
import com.example.chatappstarting.constants.USERS_CONNECTED
import com.example.chatappstarting.data.room.model.UserInfo
import com.example.chatappstarting.data.room.model.UserInformation
import com.example.chatappstarting.presentation.ui.home.model.StatusEnum
import com.example.chatappstarting.presentation.utils.mapInfo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.gson.Gson
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.util.Date
import javax.inject.Inject

class FireBaseClient @Inject constructor(private val db: FirebaseFirestore) {

    private val TAG = "Firebase Client"

    private val gson = Gson()

    fun login(
        uname: String,
        onSendPassword: (UserInformation) -> Unit = {},
        onUserNotExist: () -> Unit = {}
    ) {
        val docRef = db.collection("letsChatDbMain").document(uname)

        docRef.get()
            .addOnSuccessListener {
                val data = it.data

                Log.d(TAG, it.toString())

                if (data == null)
                    onUserNotExist()
                else {
                    val info = Gson().fromJson(Gson().toJson(data), UserInfo::class.java)
                    val token = generateToken(info.user_name, info.password)
                    setLoginToken(
                        info.user_name,
                        token,
                        onSuccess = {
                            onSendPassword(info.mapInfo().copy(token = token))
                        }
                    )
                }
            }
    }

    fun createUser(mobile: String, pass: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val user = UserInfo(
            name = mobile,
            password = pass,
            user_name = mobile,
            status = "online",
            users_connected = listOf("")
        )
        val docRef = db.collection("letsChatDbMain")

        docRef.document(mobile)
            .set(user)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure()
            }
    }

    fun observeUserStatus(listener: (List<UserInformation>?) -> Unit = {}) {
        val docRef = db.collection("letsChatDbMain")

        var list: List<UserInfo>?
        docRef.addSnapshotListener { value, _ ->
            list = value?.documents?.map {
                Log.d(TAG, it.toString())
                gson.fromJson(gson.toJson(it.data), UserInfo::class.java)
            }
            listener(
                list?.map { it.mapInfo() }
            )
        }
    }

    fun observeLoginStatus(uname: String, currentToken: String, listener: (Boolean) -> Unit = {}) {
        val docRef = db.collection("letsChatDbMain").document(uname)

        docRef.addSnapshotListener { value, _ ->
            val info = gson.fromJson(gson.toJson(value?.data), UserInfo::class.java)
            listener(info.token == currentToken)
        }
    }

    fun addConnection(
        uname: String,
        user: List<String>,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val docRef = db.collection("letsChatDbMain")

        docRef.document(uname)
            .set(ConnectedList(user), SetOptions.mergeFields(USERS_CONNECTED))
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                it.printStackTrace()
                onFailure()
            }
    }

    fun setStatus(uname: String, status: StatusEnum = StatusEnum.NONE) {
        val docRef = db.collection("letsChatDbMain")

        docRef.document(uname)
            .set(
                mapOf(
                    "status" to when (status) {
                        StatusEnum.ONLINE -> "online"
                        else -> "offline"
                    }
                ), SetOptions.mergeFields("status")
            )
            .addOnSuccessListener {
            }
            .addOnFailureListener {
                setStatus(uname, status)
            }
    }

    private fun setLoginToken(uname: String, token: String, onSuccess: () -> Unit = {}) {
        val docRef = db.collection("letsChatDbMain")

        docRef.document(uname)
            .set(
                mapOf(
                    LOGIN_TOKEN to token
                ), SetOptions.mergeFields(LOGIN_TOKEN)
            )
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                setLoginToken(uname, token, onSuccess)
            }
    }
}

fun generateToken(uname: String, pass: String): String {
    val jwt: String =
        Jwts.builder()
            .claim("user_name", uname)
            .claim("pass", pass.substring(0, 2) + "****")
            .claim("time", Date().time)
            .signWith(SignatureAlgorithm.HS256, "secret".toByteArray())
            .compact()
    Log.v("JWT token : - ", jwt)

    return jwt
}

private data class ConnectedList(val users_connected: List<String>)