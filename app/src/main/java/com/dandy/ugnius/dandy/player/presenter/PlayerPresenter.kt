package com.dandy.ugnius.dandy.player.presenter

import com.dandy.ugnius.dandy.player.view.PlayerView
import com.spotify.sdk.android.player.SpotifyPlayer
import javax.inject.Inject

class PlayerPresenter @Inject constructor(private val player: SpotifyPlayer, private val playerView: PlayerView) {

    fun playTrack(trackId: String) {
        //track is playing!!
        player.playUri(null, "spotify:track:$trackId", 0, 0)
    }
}