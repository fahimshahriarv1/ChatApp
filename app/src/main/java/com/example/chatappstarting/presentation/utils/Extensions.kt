package com.example.chatappstarting.presentation.utils

import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity
import com.example.chatappstarting.data.room.model.UserInfo
import com.example.chatappstarting.data.room.model.UserInformation
import com.example.chatappstarting.presentation.ui.home.model.StatusEnum

fun Boolean?.falseIfNull(): Boolean {
    return this ?: false
}

fun Boolean?.trueIfNull(): Boolean {
    return this ?: true
}

fun UserInfo.mapInfo(): UserInformation {
    return UserInformation(
        name = this.name,
        usersConnected = this.users_connected,
        userName = this.user_name,
        password = this.password,
        status = when (this.status) {
            "online" -> StatusEnum.ONLINE
            else -> StatusEnum.OFFLINE
        },
        token = this.token
    )
}

fun Context.getActivity(): AppCompatActivity? = when (this) {
    is AppCompatActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}