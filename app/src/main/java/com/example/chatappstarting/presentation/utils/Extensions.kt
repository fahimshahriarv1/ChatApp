package com.example.chatappstarting.presentation.utils

fun Boolean?.falseIfNull(): Boolean {
    return this ?: false
}

fun Boolean?.trueIfNull(): Boolean {
    return this ?: true
}