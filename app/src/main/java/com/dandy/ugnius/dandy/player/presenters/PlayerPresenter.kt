package com.dandy.ugnius.dandy.player.presenters

import android.os.Bundle
import android.os.Handler
import com.dandy.ugnius.dandy.utilities.random
import com.dandy.ugnius.dandy.global.entities.Track
import com.dandy.ugnius.dandy.player.view.PlayerView
import com.spotify.sdk.android.player.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashSet

class PlayerPresenter @Inject constructor(private val playerView: PlayerView, private val player: SpotifyPlayer) {

    private lateinit var tracks: LinkedList<Track>
    private lateinit var currentTrack: Track
    private var queue = LinkedHashSet<Track>()
    private var shuffle: Boolean = false
    private var isPaused = false
    private lateinit var unmodifiableTracks: LinkedList<Track>
    private val handler = Handler()
    private var replay: Boolean = false
    private val runnable = object : Runnable {
        override fun run() {
            if (playerView.hasTrackEnded()) {
                skipToNext()
                return
            } else if (!isPaused) {
                playerView.updateProgress()
            }
            handler.postDelayed(this, 1000)
        }
    }

    fun skipToNext(): Track {
        if (replay && playerView.hasTrackEnded()) {
            playTrack()
        } else {
            val nextTrackIndex = if (shuffle) {
                val hasQueueEnded = !queue.contains(currentTrack) || queue.indexOf(currentTrack) == queue.size - 1
                if (hasQueueEnded) {
                    if (tracks.size == 1) 0 else random(0, tracks.size - 1)
                } else {
                    val nextQueuedTrackIndex = queue.indexOf(currentTrack) + 1
                    currentTrack = queue.elementAt(nextQueuedTrackIndex)
                    playTrack()
                    return currentTrack
                }
            } else {
                val allTracksHaveBeenPlayed = tracks.indexOf(currentTrack) == tracks.lastIndex
                if (allTracksHaveBeenPlayed) {
                    if (shuffle) {
                        random(0, tracks.size - 1)
                    } else {
                        resetPlaybackQueue()
                        0
                    }
                } else {
                    tracks.indexOf(currentTrack) + 1
                }
            }
            val isValidIndex = nextTrackIndex != tracks.size
            if (isValidIndex) {
                currentTrack = tracks[nextTrackIndex]
                playTrack()
            }
        }
        return currentTrack
    }

    fun skipToPrevious(): Track {
        if (playerView.shouldRewind()) {
            playTrack()
        } else {
            if (shuffle) {
                val lastTrackIndex = queue.indexOf(currentTrack) - 1
                currentTrack = if (lastTrackIndex == -1) queue.first() else queue.elementAt(lastTrackIndex)
                playTrack()
            } else {
                val previousTrackIndex = tracks.indexOf(currentTrack) - 1
                currentTrack = if (previousTrackIndex == -1) tracks.first() else tracks[previousTrackIndex]
                playTrack()
            }
        }
        return currentTrack
    }

    /**
     * Plays a specified track, adds it to queue for shuffling and also pauses current playback,
     * this is because user can move forwards/backwards very fast and the song will keep playing,
     * this should be implemented by using a callback however there is this issue https://github.com/spotify/android-sdk/issues/419
     */

    fun playTrack() {
        currentTrack.let {
            if (shuffle) {
                queue.add(it)
                tracks.remove(it)
                if (tracks.isEmpty()) {
                    resetPlaybackQueue()
                }
            }
            player.pause(null)
            player.playUri(null, it.uri, 0, 0)
            handler.removeCallbacks(runnable)
            handler.postDelayed(runnable, 300)
            playerView.update(currentTrack)
            isPaused = false
            playerView.togglePlayButton(isPaused)
        }
    }

    fun seekToPosition(position: Int) {
        player.pause(null)
        player.seekToPosition(null, position * 1000)
        Handler().postDelayed({
            player.resume(null)
            isPaused = false
            playerView.togglePlayButton(isPaused)
        }, 300)
    }

    fun togglePlayback() {
        isPaused = if (isPaused) {
            player.resume(null)
            false
        } else {
            player.pause(null)
            true
        }
        playerView.togglePlayButton(isPaused)
    }

    fun toggleShuffle() {
        if (shuffle) {
            shuffle = false
            resetPlaybackQueue()
        } else {
            val playedTracks = tracks.slice(0 until tracks.indexOf(currentTrack) + 1)
            queue.addAll(playedTracks)
            tracks.removeAll(playedTracks)
            shuffle = true
        }
        playerView.toggleShuffle(shuffle)
    }

    fun toggleReplay() {
        replay = !replay
        playerView.toggleReplay(replay)
    }

    private fun resetPlaybackQueue() {
        tracks = LinkedList(unmodifiableTracks)
        queue.clear()
    }

    fun setState(bundle: Bundle) {
        with(bundle) {
            tracks = LinkedList(getParcelableArrayList("tracks"))
            currentTrack = getParcelable("currentTrack")
            shuffle = getBoolean("shuffle")
            isPaused = getBoolean("isPaused")
            queue = LinkedHashSet(getParcelableArrayList("queue") ?: emptyList())
            replay = getBoolean("replay")
            unmodifiableTracks = LinkedList(tracks)
        }
    }

    fun saveState(bundle: Bundle) {
        with(bundle) {
            putParcelableArrayList("tracks", ArrayList(tracks))
            putParcelable("currentTrack", currentTrack)
            putBoolean("shuffle", shuffle)
            putBoolean("isPaused", isPaused)
            putParcelableArrayList("queue", ArrayList(queue))
            putBoolean("replay", replay)   
        }
    }

}