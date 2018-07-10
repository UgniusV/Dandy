package com.dandy.ugnius.dandy.player.presenter

import com.dandy.ugnius.dandy.model.entities.Track
import com.dandy.ugnius.dandy.player.view.PlayerView
import com.spotify.sdk.android.player.*

class PlayerPresenter(private val player: SpotifyPlayer, private val playerView: PlayerView) {

    /**
     * Plays a specified track, also pauses current playback, this is because user can move
     * forwards/backwards very fast and the song will keep playing, this should be implemented
     * by using a callback however there is this issue https://github.com/spotify/android-sdk/issues/419
     */
    fun playTrack(trackUri: String) {
        player.pause(null)
        player.playUri(null, trackUri, 0, 0)
    }

    fun skipToNext(currentTrackId: String, tracks: List<Track>) {
        val nextTrackIndex = tracks.indexOf(tracks.find { it.id == currentTrackId }) + 1
        println("flow: next track index $nextTrackIndex")
        if (nextTrackIndex != tracks.size) {
            playerView.update(tracks[nextTrackIndex])
        }
    }

    fun skipToPrevious(currentTrackId: String, tracks: List<Track>) {
        val previousTrackIndex = tracks.indexOf(tracks.find { it.id == currentTrackId }) - 1
        if (previousTrackIndex == -1) {
            playerView.update(tracks.first())
        } else {
            playerView.update(tracks[previousTrackIndex])
        }
    }

    fun pause() {
        player.pause(null)
    }

    fun resume() {
        player.resume(null)
    }

}