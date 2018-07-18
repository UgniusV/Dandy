package com.dandy.ugnius.dandy.player.presenter

import android.os.Bundle
import android.os.Handler
import com.dandy.ugnius.dandy.model.entities.Track
import com.dandy.ugnius.dandy.player.view.PlayerView
import com.spotify.sdk.android.player.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashSet

class PlayerPresenter(private val playerView: PlayerView) {

    var player: SpotifyPlayer? = null
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
                if (tracks.indexOf(currentTrack) == tracks.lastIndex) {
                    random(0, tracks.size - 1)
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
                    tracks = LinkedList(unmodifiableTracks)
                    queue.clear()
                }
            }
            player?.pause(null)
            player?.playUri(null, it.uri, 0, 0)
            handler.removeCallbacks(runnable)
            handler.postDelayed(runnable, 300)
            playerView.update(currentTrack)
            isPaused = false
            playerView.togglePlayButton(isPaused)
        }
    }

    fun seekToPosition(position: Int) {
        player?.pause(null)
        player?.seekToPosition(null, position * 1000)
        Handler().postDelayed({
            player?.resume(null)
            isPaused = false
            playerView.togglePlayButton(isPaused)
        }, 300)
    }

    fun togglePlayback() {
        if (isPaused) {
            player?.resume(null)
            isPaused = false
        } else {
            player?.pause(null)
            isPaused = true
        }
        playerView.togglePlayButton(isPaused)
    }

    fun toggleShuffle() {
        if (shuffle) {
            shuffle = false
            tracks = LinkedList(unmodifiableTracks)
            queue.clear()
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

    fun setState(bundle: Bundle?) {
        bundle?.getParcelableArrayList<Track>("tracks")?.let {
            tracks = LinkedList(it)
            unmodifiableTracks = LinkedList(it)
        }
        bundle?.getParcelable<Track>("currentTrack")?.let { currentTrack = it }
        bundle?.getBoolean("shuffle", false)?.let {
            shuffle = it
        }
        bundle?.getBoolean("isPaused", false)?.let { isPaused = it }
        bundle?.getParcelableArrayList<Track>("queue")?.let { queue = LinkedHashSet(it) }
        bundle?.getBoolean("replay")?.let { replay = it }
    }

    fun saveState(bundle: Bundle) {
        bundle.putParcelableArrayList("tracks", ArrayList(tracks))
        bundle.putParcelable("currentTrack", currentTrack)
        bundle.putBoolean("shuffle", shuffle)
        bundle.putBoolean("isPaused", isPaused)
        bundle.putParcelableArrayList("queue", ArrayList(queue))
        bundle.putBoolean("replay", replay)
    }

    private fun random(start: Int, end: Int) = Random().nextInt((end + 1) - start) +  start

}