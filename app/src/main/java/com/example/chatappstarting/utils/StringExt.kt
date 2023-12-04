package com.example.chatappstarting.utils

fun String?.emptyIfNull(): String {
    return if (isNullOrEmpty())
        ""
    else
        this
}