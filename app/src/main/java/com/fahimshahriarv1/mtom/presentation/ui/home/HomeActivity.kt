package com.fahimshahriarv1.mtom.presentation.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.fahimshahriarv1.mtom.presentation.navgraph.NavGraph
import com.fahimshahriarv1.mtom.presentation.navgraph.Route
import com.fahimshahriarv1.mtom.presentation.ui.base.BaseActivity
import com.fahimshahriarv1.mtom.presentation.ui.theme.MtoMTheme

class HomeActivity : BaseActivity() {

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* granted or not */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideToolbar()
        hideSystemUI()
        requestNotificationPermission()
        val chatId = intent?.getStringExtra("chat_id")
        val recipientName = intent?.getStringExtra("recipient_name")

        setContent {
            MtoMTheme {
                NavGraph(
                    startDest = Route.AppMain.route,
                    chatId = chatId,
                    recipientName = recipientName
                )
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}