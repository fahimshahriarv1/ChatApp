package com.fahimshahriarv1.mtom.data.firebase

import android.util.Log
import com.fahimshahriarv1.mtom.constants.LOGIN_TOKEN
import com.fahimshahriarv1.mtom.constants.PASSWORD
import com.fahimshahriarv1.mtom.constants.USERS_CONNECTED
import com.fahimshahriarv1.mtom.data.room.model.UserInfo
import com.fahimshahriarv1.mtom.data.room.model.UserInformation
import com.fahimshahriarv1.mtom.domain.model.StatusEnum
import com.fahimshahriarv1.mtom.presentation.utils.hashPassword
import com.fahimshahriarv1.mtom.presentation.utils.mapInfo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.gson.Gson
import java.security.MessageDigest
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
        password: String,
        onSuccess: (UserInformation) -> Unit = {},
        onUserNotExist: () -> Unit = {},
        onWrongPassword: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        logApiRequest("login", "uname=$uname, password=***")
        dbRef.document(uname)
            .get()
            .addOnSuccessListener {
                val data = it.data

                Log.d(TAG, it.toString())

                if (data == null) {
                    logApiResponse("login", "user does not exist")
                    onUserNotExist()
                } else {
                    val info = Gson().fromJson(Gson().toJson(data), UserInfo::class.java)
                    val hashedInput = hashPassword(password)

                    if (info.password != hashedInput) {
                        logApiResponse("login", "password mismatch for $uname")
                        onWrongPassword()
                        return@addOnSuccessListener
                    }

                    logApiResponse(
                        "login",
                        "uname=${info.user_name}, name=${info.name}, " +
                                "connections=${info.users_connected.size}"
                    )

                    val token = generateToken(info.user_name, info.password)
                    setLoginToken(
                        info.user_name,
                        token,
                        onSuccess = {
                            onSuccess(info.mapInfo().copy(token = token))
                        }
                    )
                }
            }
            .addOnFailureListener {
                // Without this the loader spins forever when Firestore is
                // unreachable (offline emulator, no DNS, rules rejection).
                logApiError("login", it)
                onFailure(it)
            }
    }

    fun createUser(mobile: String, pass: String, onSuccess: (String) -> Unit, onFailure: () -> Unit) {
        val hashedPass = hashPassword(pass)
        val user = UserInfo(
            name = mobile,
            password = hashedPass,
            user_name = mobile,
            status = "online",
            users_connected = listOf("")
        )

        logApiRequest("createUser", "mobile=$mobile, password=***")
        dbRef.document(mobile)
            .set(user)
            .addOnSuccessListener {
                logApiResponse("createUser", "document $mobile written")
                val token = generateToken(mobile, hashedPass)
                setLoginToken(mobile, token) {
                    onSuccess(token)
                }
            }
            .addOnFailureListener {
                logApiError("createUser", it)
                onFailure()
            }
    }

    fun updatePassword(
        uname: String,
        newPassword: String,
        onSuccess: () -> Unit = {},
        onFailure: () -> Unit = {}
    ) {
        val hashedPass = hashPassword(newPassword)
        // A fresh token is written along with the password so any session still
        // holding the old token gets logged out by observeLoginStatus.
        val token = generateToken(uname, hashedPass)

        logApiRequest("updatePassword", "uname=$uname, password=***")
        dbRef.document(uname)
            .set(
                mapOf(
                    PASSWORD to hashedPass,
                    LOGIN_TOKEN to token
                ), SetOptions.mergeFields(PASSWORD, LOGIN_TOKEN)
            )
            .addOnSuccessListener {
                logApiResponse("updatePassword", "password and token updated for $uname")
                onSuccess()
            }
            .addOnFailureListener {
                logApiError("updatePassword", it)
                it.printStackTrace()
                onFailure()
            }
    }

    fun observeUserStatus(listener: (List<UserInformation>?) -> Unit = {}) {
        var list: List<UserInfo>?
        logApiRequest("observeUserStatus", "collection snapshot listener attached")
        connectionListener =
            dbRef.addSnapshotListener { value, error ->
                if (error != null) {
                    logApiError("observeUserStatus", error)
                    return@addSnapshotListener
                }
                logApiResponse("observeUserStatus", "${value?.documents?.size ?: 0} users")
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
        logApiRequest("observeLoginStatus", "uname=$uname")
        tokenStatusListener =
            dbRef.document(uname)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        logApiError("observeLoginStatus", error)
                        return@addSnapshotListener
                    }
                    val info = gson.fromJson(gson.toJson(value?.data), UserInfo::class.java)
                    val isSameSession = info.token == currentToken
                    logApiResponse("observeLoginStatus", "sessionStillValid=$isSameSession")
                    listener(isSameSession)
                }
    }

    fun addConnection(
        uname: String,
        user: List<String>,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        logApiRequest("addConnection", "uname=$uname, connections=${user.size}")
        dbRef.document(uname)
            .update(USERS_CONNECTED, user)
            .addOnSuccessListener {
                logApiResponse("addConnection", "connections updated for $uname")
                onSuccess()
            }
            .addOnFailureListener {
                logApiError("addConnection", it)
                it.printStackTrace()
                onFailure()
            }
    }

    fun addMutualConnection(userA: String, userB: String, onDone: () -> Unit) {
        logApiRequest("addMutualConnection", "userA=$userA, userB=$userB")
        // Add B to A's list, then A to B's list
        dbRef.document(userA).get().addOnSuccessListener { docA ->
            val infoA = gson.fromJson(gson.toJson(docA.data), UserInfo::class.java)
            val listA = (infoA?.users_connected ?: listOf()).toMutableList()
            if (userB !in listA) listA.add(userB)
            listA.remove("")

            dbRef.document(userA)
                .update(USERS_CONNECTED, listA)
                .addOnSuccessListener {
                    logApiResponse("addMutualConnection", "$userB added to $userA")
                    // Now add A to B's list
                    dbRef.document(userB).get().addOnSuccessListener { docB ->
                        val infoB = gson.fromJson(gson.toJson(docB.data), UserInfo::class.java)
                        val listB = (infoB?.users_connected ?: listOf()).toMutableList()
                        if (userA !in listB) listB.add(userA)
                        listB.remove("")

                        dbRef.document(userB)
                            .update(USERS_CONNECTED, listB)
                            .addOnSuccessListener {
                                logApiResponse("addMutualConnection", "$userA added to $userB")
                                onDone()
                            }
                            .addOnFailureListener {
                                logApiError("addMutualConnection", it)
                                onDone()
                            }
                    }.addOnFailureListener {
                        logApiError("addMutualConnection", it)
                        onDone()
                    }
                }
                .addOnFailureListener {
                    logApiError("addMutualConnection", it)
                    onDone()
                }
        }.addOnFailureListener {
            logApiError("addMutualConnection", it)
            onDone()
        }
    }

    fun setStatus(uname: String, status: StatusEnum = StatusEnum.NONE) {
        logApiRequest("setStatus", "uname=$uname, status=$status")
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
                logApiResponse("setStatus", "status=$status written for $uname")
            }
            .addOnFailureListener {
                logApiError("setStatus", it)
                setStatus(uname, status)
            }
    }

    private fun setLoginToken(uname: String, token: String, onSuccess: () -> Unit = {}) {
        logApiRequest("setLoginToken", "uname=$uname")
        dbRef.document(uname)
            .set(
                mapOf(
                    LOGIN_TOKEN to token
                ), SetOptions.mergeFields(LOGIN_TOKEN)
            )
            .addOnSuccessListener {
                logApiResponse("setLoginToken", "token written for $uname")
                onSuccess()
            }
            .addOnFailureListener {
                logApiError("setLoginToken", it)
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
        logApiRequest("getUserPassword", "uname=$uname")
        dbRef.document(uname).get()
            .addOnSuccessListener {
                val data = it.data
                if (data != null) {
                    val info = Gson().fromJson(Gson().toJson(data), UserInfo::class.java)
                    logApiResponse("getUserPassword", "hash found for $uname")
                    onResult(info.password)
                } else {
                    logApiResponse("getUserPassword", "no document for $uname")
                    onResult(null)
                }
            }
            .addOnFailureListener {
                logApiError("getUserPassword", it)
                onResult(null)
            }
    }

    fun fetchConnectionList(uname: String, onResult: (List<String>) -> Unit) {
        logApiRequest("fetchConnectionList", "uname=$uname")
        dbRef.document(uname).get()
            .addOnSuccessListener {
                val data = it.data
                if (data != null) {
                    val info = gson.fromJson(gson.toJson(data), UserInfo::class.java)
                    val connections = info.users_connected.filter { c -> c.isNotEmpty() }
                    logApiResponse("fetchConnectionList", "${connections.size} connections")
                    onResult(connections)
                } else {
                    logApiResponse("fetchConnectionList", "no document for $uname")
                    onResult(emptyList())
                }
            }
            .addOnFailureListener {
                logApiError("fetchConnectionList", it)
                Log.e(TAG, "Failed to fetch connections: ${it.message}")
                onResult(emptyList())
            }
    }

    fun checkUserExistence(
        uname: String,
        existOrNot: (Boolean) -> Unit = {},
        onFailed: (Exception) -> Unit = {}
    ) {
        logApiRequest("checkUserExistence", "uname=$uname")
        dbRef.document(uname)
            .get()
            .addOnSuccessListener {
                val exists = !it.data.isNullOrEmpty()
                logApiResponse("checkUserExistence", "exists=$exists")
                existOrNot(exists)
            }
            .addOnFailureListener {
                logApiError("checkUserExistence", it)
                onFailed(it)
            }
    }
}

fun generateToken(uname: String, pass: String): String {
    val raw = "$uname:${pass.take(2)}****:${Date().time}"
    val digest = MessageDigest.getInstance("SHA-256")
    val hash = digest.digest(raw.toByteArray(Charsets.UTF_8))
    val token = hash.joinToString("") { "%02x".format(it) }
    Log.v("Token", token)
    return token
}
