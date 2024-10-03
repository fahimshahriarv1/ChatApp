package com.example.chatappstarting.presentation.ui.utils

import android.content.Context
import android.widget.Toast


fun showToastMessage(msg: String, context: Context) {
    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
}