package com.dandy.ugnius.dandy.player.utilities

import com.spotify.sdk.android.player.SpotifyPlayer
import javax.inject.Inject

class PlaybackManager @Inject constructor(private val player: SpotifyPlayer) {

    fun play(trackId: String) {
        player.playUri(null, "spotify:track:$trackId", 0, 0)
    }

    fun pause() {

    }

    fun resume() {

    }

    fun seekToPosition(position: Float) {

    }

    fun login() {

    }

    fun logout() {

    }
}