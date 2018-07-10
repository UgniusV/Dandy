package com

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.Notification.VISIBILITY_PUBLIC
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import com.dandy.ugnius.dandy.di.components.DaggerMainComponent
import com.dandy.ugnius.dandy.di.components.MainComponent
import com.dandy.ugnius.dandy.di.modules.UtilitiesModule
import com.dandy.ugnius.dandy.player.receiver.StreamingNotificationReceiver
import android.net.ConnectivityManager.CONNECTIVITY_ACTION
import com.dandy.ugnius.dandy.player.receivers.ConnectionStateReceiver
import org.greenrobot.eventbus.EventBus

class App : Application() {

    var mainComponent: MainComponent? = null

    companion object {
        const val CHANNEL_ID = "12345"
        const val NOTIFICATION_REQUEST_CODE = 2
        const val NOTIFICATION_ACTION_PLAY = "com.dandy.NOTIFICATION_ACTION_PLAY"
        const val NOTIFICATION_ACTION_PAUSE = "com.dandy.NOTIFICATION_ACTION_PAUSE"
        const val NOTIFICATION_ACTION_PREVIOUS = "com.dandy.NOTIFICATION_ACTION_PREVIOUS"
        const val NOTIFICATION_ACTION_NEXT = "com.dandy.NOTIFICATION_ACTION_NEXT"
    }

    override fun onCreate() {
        super.onCreate()
        createDependencyGraph()
        createNotificationChannelIfNeeded()
        val intentFilter = IntentFilter().apply {
            addAction(NOTIFICATION_ACTION_PLAY)
            addAction(NOTIFICATION_ACTION_PAUSE)
            addAction(NOTIFICATION_ACTION_PREVIOUS)
            addAction(NOTIFICATION_ACTION_NEXT)
        }
        registerReceiver(StreamingNotificationReceiver(), intentFilter)
        registerReceiver(ConnectionStateReceiver(), IntentFilter(CONNECTIVITY_ACTION))
    }

    private fun createDependencyGraph() {
        mainComponent = DaggerMainComponent.builder()
            .utilitiesModule(UtilitiesModule(this))
            .build()
    }

    /**
     * you must create the notification channel before posting any notifications on Android 8.0 and higher,
     * you should execute this code as soon as your app starts.
     * It's safe to call this repeatedly because creating an existing notification channel performs no operation
     */
    private fun createNotificationChannelIfNeeded() {
        if (SDK_INT >= O) {
            val channelName = "Music"
            val channelDescription = "It is not recommended to disable notifications because you won't be able to access media player controls and control your music playback via notification center"
            val notificationChannel = NotificationChannel(CHANNEL_ID, channelName, IMPORTANCE_LOW)
            notificationChannel.description = channelDescription
            notificationChannel.lockscreenVisibility = VISIBILITY_PUBLIC
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}