package com.dandy.ugnius.dandy.player.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.support.v4.app.NotificationCompat
import com.App
import com.App.Companion.NOTIFICATION_ACTION_NEXT
import com.App.Companion.NOTIFICATION_ACTION_PAUSE
import com.App.Companion.NOTIFICATION_ACTION_PLAY
import com.App.Companion.NOTIFICATION_ACTION_PREVIOUS
import com.dandy.ugnius.dandy.R
import com.dandy.ugnius.dandy.di.modules.UtilitiesModule
import com.dandy.ugnius.dandy.player.utilities.PlaybackManager
import javax.inject.Inject

/**
 * A broadcast received used to react to music playback notification actions
 */

class StreamingNotificationReceiver : BroadcastReceiver() {

    @Inject lateinit var playbackManager: PlaybackManager
    @Inject lateinit var notificationBuilder: NotificationCompat.Builder

    override fun onReceive(context: Context, intent: Intent) {
        (context as App).mainComponent?.inject(this)
        when (intent.action) {
            NOTIFICATION_ACTION_PLAY -> {
                //play
            }
            NOTIFICATION_ACTION_PAUSE -> {
                //pause
            }
            NOTIFICATION_ACTION_PREVIOUS -> {
                //previous
            }
            NOTIFICATION_ACTION_NEXT -> {
                intent.extras.getString("nextTrackId")?.let { playbackManager.play(it) }
                notificationBuilder.setLargeIcon()
            }
        }
    }
}
