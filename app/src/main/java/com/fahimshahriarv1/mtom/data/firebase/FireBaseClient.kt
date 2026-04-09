package com.fahimshahriarv1.mtom.data.firebase

import android.util.Log
import com.fahimshahriarv1.mtom.constants.LOGIN_TOKEN
import com.fahimshahriarv1.mtom.constants.USERS_CONNECTED
import com.fahimshahriarv1.mtom.data.room.model.UserInfo
import com.fahimshahriarv1.mtom.data.room.model.UserInformation
import com.fahimshahriarv1.mtom.domain.model.StatusEnum
import com.fahimshahriarv1.mtom.presentation.utils.mapInfo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.gson.Gson
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.util.Date
import javax.inject.Inject

class FireBaseClient @Inject constructor(db: FirebaseFirestore) {

    private val TAG = "Firebase Client"

    private val gson = Gson()
    private val dbRef = db.collection("letsChatDbMain")
    private lateinit var connectionListener: ListenerRegistration
    private lateinit var tokenStatusListener: ListenerRegistration

    fun login(
        uname: String,
        onSendPassword: (UserInformation, () -> Unit) -> Unit = { _, _ -> },
        onUserNotExist: () -> Unit = {},
        onSuccess: () -> Unit = {}
    ) {
        dbRef.document(uname)
            .get()
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
                            onSendPassword(info.mapInfo().copy(token = token), onSuccess)
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

        dbRef.document(mobile)
            .set(user)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure()
            }
    }

    fun observeUserStatus(listener: (List<UserInformation>?) -> Unit = {}) {
        var list: List<UserInfo>?
        Log.d("listening", "connections listening started")
        connectionListener =
            dbRef.addSnapshotListener { value, _ ->
                Log.d("listening", "connections")
                list = value?.documents?.map {
                    Log.d(TAG, it.toString())
                    gson.fromJson(gson.toJson(it.data), UserInfo::class.java)
                }
                listener(
                    list?.map { it.mapInfo() }
                )
            }
    }

    fun observeLoginStatus(
        uname: String,
        currentToken: String,
        listener: (Boolean) -> Unit = {}
    ) {
        Log.d("listening", "starting token observe")
        tokenStatusListener =
            dbRef.document(uname)
                .addSnapshotListener { value, _ ->
                    Log.d("listening", "loginState")
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
        dbRef.document(uname)
            .set(ConnectedList(user), SetOptions.mergeFields(USERS_CONNECTED))
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                it.printStackTrace()
                onFailure()
            }
    }

    fun addMutualConnection(userA: String, userB: String, onDone: () -> Unit) {
        // Add B to A's list, then A to B's list
        dbRef.document(userA).get().addOnSuccessListener { docA ->
            val infoA = gson.fromJson(gson.toJson(docA.data), UserInfo::class.java)
            val listA = (infoA?.users_connected ?: listOf()).toMutableList()
            if (userB !in listA) listA.add(userB)
            listA.remove("") // clean empty entries

            dbRef.document(userA)
                .set(ConnectedList(listA), SetOptions.mergeFields(USERS_CONNECTED))
                .addOnSuccessListener {
                    // Now add A to B's list
                    dbRef.document(userB).get().addOnSuccessListener { docB ->
                        val infoB = gson.fromJson(gson.toJson(docB.data), UserInfo::class.java)
                        val listB = (infoB?.users_connected ?: listOf()).toMutableList()
                        if (userA !in listB) listB.add(userA)
                        listB.remove("")

                        dbRef.document(userB)
                            .set(ConnectedList(listB), SetOptions.mergeFields(USERS_CONNECTED))
                            .addOnSuccessListener { onDone() }
                            .addOnFailureListener { onDone() }
                    }.addOnFailureListener { onDone() }
                }
                .addOnFailureListener { onDone() }
        }.addOnFailureListener { onDone() }
    }

    fun setStatus(uname: String, status: StatusEnum = StatusEnum.NONE) {
        dbRef.document(uname)
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
        dbRef.document(uname)
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

    fun removeListeners() {
        if (::connectionListener.isInitialized)
            connectionListener.remove()
        if (::tokenStatusListener.isInitialized)
            tokenStatusListener.remove()
    }

    fun getUserPassword(uname: String, onResult: (String?) -> Unit) {
        dbRef.document(uname).get()
            .addOnSuccessListener {
                val data = it.data
                if (data != null) {
                    val info = Gson().fromJson(Gson().toJson(data), UserInfo::class.java)
                    onResult(info.password)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun fetchConnectionList(uname: String, onResult: (List<String>) -> Unit) {
        dbRef.document(uname).get()
            .addOnSuccessListener {
                val data = it.data
                if (data != null) {
                    val info = gson.fromJson(gson.toJson(data), UserInfo::class.java)
                    val connections = info.users_connected.filter { c -> c.isNotEmpty() }
                    onResult(connections)
                } else {
                    onResult(emptyList())
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "Failed to fetch connections: ${it.message}")
                onResult(emptyList())
            }
    }

    fun checkUserExistence(
        uname: String,
        existOrNot: (Boolean) -> Unit = {},
        onFailed: (Exception) -> Unit = {}
    ) {

        dbRef.document(uname)
            .get()
            .addOnSuccessListener {
                if (it.data.isNullOrEmpty())
                    existOrNot(false)
                else
                    existOrNot(true)
            }
            .addOnFailureListener {
                onFailed(it)
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