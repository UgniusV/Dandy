package com.dandy.ugnius.dandy.player.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import com.dandy.ugnius.dandy.global.app.App.Companion.NOTIFICATION_ACTION_NEXT
import com.dandy.ugnius.dandy.global.app.App.Companion.NOTIFICATION_ACTION_PAUSE
import com.dandy.ugnius.dandy.global.app.App.Companion.NOTIFICATION_ACTION_PLAY
import com.dandy.ugnius.dandy.global.app.App.Companion.NOTIFICATION_ACTION_PREVIOUS
import com.spotify.sdk.android.player.SpotifyPlayer
import javax.inject.Inject

/**
 * A broadcast received used to react to music playback notification actions
 */

//this will be used later
class StreamingNotificationReceiver : BroadcastReceiver() {

    @Inject lateinit var player: SpotifyPlayer
    @Inject lateinit var notificationBuilder: NotificationCompat.Builder

    override fun onReceive(context: Context, intent: Intent) {
//        (context as App).mainComponent?.inject(this)
        when (intent.action) {
            NOTIFICATION_ACTION_PLAY -> player.resume(null)
            NOTIFICATION_ACTION_PAUSE -> player.pause(null)
            NOTIFICATION_ACTION_PREVIOUS -> player.skipToPrevious(null)
            NOTIFICATION_ACTION_NEXT -> player.skipToNext(null)
        }
    }
}