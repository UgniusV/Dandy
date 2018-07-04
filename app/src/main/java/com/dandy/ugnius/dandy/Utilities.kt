package com.dandy.ugnius.dandy

import android.content.Context
import android.media.session.MediaSession
import android.support.v4.app.NotificationCompat
import com.App.Companion.CHANNEL_ID
import android.support.v4.app.NotificationCompat.PRIORITY_LOW

object Utilities {

    fun getScopes() = arrayOf(
        "user-library-read",
        "user-library-modify",
        "playlist-read-private",
        "playlist-modify-public",
        "playlist-modify-private",
        "playlist-read-collaborative",
        "user-read-recently-played",
        "user-top-read",
        "user-read-private",
        "user-read-email",
        "user-read-birthdate",
        "streaming",
        "user-modify-playback-state",
        "user-read-currently-playing",
        "user-read-playback-state",
        "user-follow-modify",
        "user-follow-read"
    )
}