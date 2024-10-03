package com.example.chatappstarting

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.example.chatappstarting.data.room.LocalDatabase
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class LetsChatApplication : Application() {

    @Inject
    lateinit var db: LocalDatabase

    override fun onCreate() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(p0: Activity, p1: Bundle?) {
            }

            override fun onActivityStarted(p0: Activity) {
            }

            override fun onActivityResumed(p0: Activity) {
            }

            override fun onActivityPaused(p0: Activity) {
            }

            override fun onActivityStopped(p0: Activity) {
            }

            override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
            }

            override fun onActivityDestroyed(p0: Activity) {
            }
        })
        super.onCreate()
    }

    override fun onTerminate() {
        db.close()
        super.onTerminate()
    }
}