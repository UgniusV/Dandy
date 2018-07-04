package com.dandy.ugnius.dandy.player.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import com.App
import com.App.Companion.NOTIFICATION_ACTION_NEXT
import com.App.Companion.NOTIFICATION_ACTION_PAUSE
import com.App.Companion.NOTIFICATION_ACTION_PLAY
import com.App.Companion.NOTIFICATION_ACTION_PREVIOUS
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.dandy.ugnius.dandy.R
import com.dandy.ugnius.dandy.artist.model.entities.Track
import com.dandy.ugnius.dandy.di.modules.UtilitiesModule
import com.dandy.ugnius.dandy.player.utilities.PlaybackManager
import javax.inject.Inject

/**
 * A broadcast received used to react to music playback notification actions
 */

class StreamingNotificationReceiver : BroadcastReceiver() {

    @Inject
    lateinit var playbackManager: PlaybackManager
    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder

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
                intent.extras.getParcelable<Track>("nextTrack")?.let {
                    playbackManager.play(it.id!!)
                    val image = it.images?.first()
                    try {
                        Glide.with(context).asBitmap().load(image)
                            .into(object : SimpleTarget<Bitmap>() {
                                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                    notificationBuilder.setLargeIcon(resource)
                                        .setContentTitle(it.name)
                                        .setContentText(it.artists)
                                    NotificationManagerCompat.from(context).notify(1, notificationBuilder.build())
                                }
                            })
                    } catch (ex: Exception) {
                        Log.wtf("wtf", ex)
                    }
                }
            }
        }
    }
}
