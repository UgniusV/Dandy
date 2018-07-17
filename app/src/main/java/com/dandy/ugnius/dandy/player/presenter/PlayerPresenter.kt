package com.dandy.ugnius.dandy.player.presenter

import android.os.Bundle
import android.os.Handler
import com.dandy.ugnius.dandy.Utilities
import com.dandy.ugnius.dandy.artist.common.random
import com.dandy.ugnius.dandy.artist.common.removeWithIndex
import com.dandy.ugnius.dandy.model.entities.Track
import com.dandy.ugnius.dandy.player.view.PlayerView
import com.spotify.sdk.android.player.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.view_player.*
import java.util.*
import java.util.concurrent.TimeUnit

class PlayerPresenter(private val playerView: PlayerView) {

    //todo test this with shuffle toggling

    //isimti is linked listo
    //todo refactor this later with DI
    var player: SpotifyPlayer? = null
    private lateinit var tracks: LinkedList<Track>
    private lateinit var currentTrack: Track
    private var queue = LinkedHashSet<Track>()
    var shuffle: Boolean = false
    private var isPaused = false
    private lateinit var unmodifiableTracks: LinkedList<Track>
    private val handler = Handler()
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

    /**
     * Stata geriau saugoti presenteryje nes viewui maziau darbo uzkrausim
     */

    fun skipToNext() {
        val nextTrackIndex = if (shuffle) {
            val hasQueueEnded = !queue.contains(currentTrack) || queue.indexOf(currentTrack) == queue.size - 1
            if (hasQueueEnded) {
                if (tracks.size == 1) 0 else (0 until tracks.size - 1).random()
            } else {
                val nextQueuedTrackIndex = queue.indexOf(currentTrack) + 1
                currentTrack = queue.elementAt(nextQueuedTrackIndex)
                println("flow: queue next index $nextQueuedTrackIndex")
                playTrack()
                return
            }
        } else {
            if (tracks.indexOf(currentTrack) == tracks.lastIndex) {
                (0 until tracks.size - 1).random()
            } else {
                tracks.indexOf(currentTrack) + 1
            }
        }
        val isValidIndex = nextTrackIndex != tracks.size
        if (isValidIndex) {
            println("flow: next index $nextTrackIndex")
            currentTrack = tracks[nextTrackIndex]
            playTrack()
        }
    }

    fun skipToPrevious() {
        if (playerView.shouldRewind()) {
            playTrack()
        } else {
            if (shuffle) {
                val lastTrackIndex = queue.indexOf(currentTrack) - 1
                currentTrack = if (lastTrackIndex == -1) queue.first() else queue.elementAt(lastTrackIndex)
                println("flow: queue previous index $lastTrackIndex")
                playTrack()
            } else {
                val previousTrackIndex = tracks.indexOf(currentTrack) - 1
                currentTrack = if (previousTrackIndex == -1) tracks.first() else tracks[previousTrackIndex]
                println("flow: previous track $previousTrackIndex")
                playTrack()
            }
        }
    }

    /**
     * Plays a specified track, adds it to queue for shuffling and also pauses current playback,
     * this is because user can move forwards/backwards very fast and the song will keep playing,
     * this should be implemented by using a callback however there is this issue https://github.com/spotify/android-sdk/issues/419
     */

    fun playTrack() {
        tracks.forEachIndexed { index, track -> println("flow: track ${index} ${track.name}") }
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
            playerView.updatePlayButton(isPaused)
        }
    }

    fun seekToPosition(position: Int) {
        player?.pause(null)
        player?.seekToPosition(null, position * 1000)
        Handler().postDelayed({
            player?.resume(null)
            isPaused = false
            playerView.updatePlayButton(isPaused)
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
        playerView.updatePlayButton(isPaused)
    }

    fun toggleShuffle() {
        if (shuffle) {
            shuffle = false
            tracks = LinkedList(unmodifiableTracks)
            queue.clear()
            println("flow: track after shuffle")
            tracks.forEachIndexed { index, track -> println("flow: track ${index} ${track.name}") }
        } else {
            shuffle = true
        }
        playerView.toggleShuffle(shuffle)
    }

    /*
    Methods used to save presenter state from activity/fragment destruction, these methods take bundle as a parameter
    but its valid because the presenter itself doesn't depend on bundle and the presenter is still completely testable
    and even more separated from the UI component
     */

    //todo save queu too

    fun setState(bundle: Bundle?) {
        bundle?.getParcelableArrayList<Track>("tracks")?.let {
            tracks = LinkedList(it)
            unmodifiableTracks = LinkedList(it)
            //todo save this
        }
        bundle?.getParcelable<Track>("currentTrack")?.let { currentTrack = it }
        bundle?.getBoolean("shuffle", shuffle)
        bundle?.getBoolean("isPaused")?.let { isPaused = it }
    }

    fun saveState(bundle: Bundle) {
        bundle.putParcelableArrayList("tracks", ArrayList(tracks))
        bundle.putParcelable("currentTrack", currentTrack)
        bundle.putBoolean("shuffle", shuffle)
        bundle.putBoolean("isPaused", isPaused)
    }

}