package com.example.chatappstarting.presentation.utils

fun String?.emptyIfNull(): String {
    return if (isNullOrEmpty())
        ""
    else
        this
}

fun String.isPhoneOperatorValid(): Boolean {
    return startsWith("013") ||
            startsWith("014") ||
            startsWith("015") ||
            startsWith("016") ||
            startsWith("017") ||
            startsWith("018") ||
            startsWith("019")
}

fun String?.isPhoneNumberValid() =
    if (isNullOrEmpty())
        false
    else
        length == 11 && isPhoneOperatorValid()