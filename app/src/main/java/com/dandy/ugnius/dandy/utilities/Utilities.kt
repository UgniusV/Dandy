package com.dandy.ugnius.dandy.utilities

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.ColorUtils
import com.dandy.ugnius.dandy.R

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

    fun durationToSeconds(duration: String): Int {
        val units = duration.split(":")
        val minutes = Integer.parseInt(units[0])
        val seconds = Integer.parseInt(units[1])
        return 60 * minutes + seconds
    }

    fun whiteBlend(context: Context, color: Int, ratio: Float): Int {
        val transparentWhite = ContextCompat.getColor(context, R.color.transparentWhite)
        return ColorUtils.blendARGB(transparentWhite, color, ratio)
    }
}