package com.example.chatappstarting.presentation.utils

fun String?.emptyIfNull(): String {
    return if (isNullOrEmpty())
        ""
    else
        this
}