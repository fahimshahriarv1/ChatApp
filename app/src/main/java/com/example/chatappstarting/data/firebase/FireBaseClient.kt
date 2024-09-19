package com.example.chatappstarting.data.firebase

import android.util.Log
import com.example.chatappstarting.data.room.model.UserInfo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import javax.inject.Inject

class FireBaseClient @Inject constructor(private val db: FirebaseFirestore) {

    private val TAG = "Firebase Client"

    private val gson = Gson()

    fun login(
        uname: String,
        onSendPassword: (UserInfo) -> Unit = {},
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
                    onSendPassword(info)
                }
            }
    }

    fun observeUserStatus(listener: (List<UserInfo>?) -> Unit = {}) {
        val docRef = db.collection("letsChatDbMain")

        var list: List<UserInfo>?
        docRef.addSnapshotListener { value, _ ->
            list = value?.documents?.map {
                Log.d(TAG, it.toString())
                gson.fromJson(gson.toJson(it.data), UserInfo::class.java)
                //UserInfo()
            }
            listener(list)
        }
    }
}