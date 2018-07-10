package com.dandy.ugnius.dandy.player.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import com.App
import com.App.Companion.NOTIFICATION_ACTION_NEXT
import com.App.Companion.NOTIFICATION_ACTION_PAUSE
import com.App.Companion.NOTIFICATION_ACTION_PLAY
import com.App.Companion.NOTIFICATION_ACTION_PREVIOUS
import com.spotify.sdk.android.player.SpotifyPlayer
import javax.inject.Inject

/**
 * A broadcast received used to react to music playback notification actions
 */

class StreamingNotificationReceiver : BroadcastReceiver() {

    @Inject lateinit var player: SpotifyPlayer
    @Inject lateinit var notificationBuilder: NotificationCompat.Builder

    override fun onReceive(context: Context, intent: Intent) {
        (context as App).mainComponent?.inject(this)
        when (intent.action) {
            NOTIFICATION_ACTION_PLAY -> player.resume(null)
            NOTIFICATION_ACTION_PAUSE -> player.pause(null)
            NOTIFICATION_ACTION_PREVIOUS -> player.skipToPrevious(null)
            NOTIFICATION_ACTION_NEXT -> player.skipToNext(null)
        }
    }
}

/*
notificationBuilder.setLargeIcon(resource)
                                        .setContentTitle(it.name)
                                        .setContentText(it.artists)
                                    NotificationManagerCompat.from(context).notify(1, notificationBuilder.build())
 */
