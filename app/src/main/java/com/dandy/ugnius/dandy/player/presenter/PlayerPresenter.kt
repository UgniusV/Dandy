package com.dandy.ugnius.dandy.player.presenter

import android.os.Bundle
import com.dandy.ugnius.dandy.artist.common.random
import com.dandy.ugnius.dandy.model.entities.Track
import com.dandy.ugnius.dandy.player.view.PlayerView
import com.spotify.sdk.android.player.*
import java.util.*

class PlayerPresenter(private val playerView: PlayerView) {

    //todo test this with shuffle toggling

    //isimti is linked listo
    //todo refactor this later with DI
    var player: SpotifyPlayer? = null
    private lateinit var tracks: LinkedList<Track>
    private lateinit var currentTrack: Track
    private var queue = LinkedHashSet<Track>()
    var shuffle: Boolean = false

    /**
     * Stata geriau saugoti presenteryje nes viewui maziau darbo uzkrausim
     */

    //pasidaryti random metoda presenteryja

    fun skipToNext() {
        val nextTrackIndex = if (shuffle) {
            val hasQueueEnded = !queue.contains(currentTrack) || queue.indexOf(currentTrack) == queue.size - 1
            if (hasQueueEnded) {
                (0 until tracks.size - 1).random()
            } else {
                val nextQueuedTrackIndex = queue.indexOf(currentTrack) + 1
                currentTrack = queue.elementAt(nextQueuedTrackIndex)
                println("flow: queue next index $nextQueuedTrackIndex")
                playTrack()
                return
            }
        } else {
            tracks.indexOf(currentTrack) + 1
        }
        val isValidIndex = nextTrackIndex != tracks.size
        if (isValidIndex) {
            println("flow: next index $nextTrackIndex")
            currentTrack = tracks[nextTrackIndex]
            playTrack()
        }
    }

    fun skipToPrevious() {
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
                //todo test this, this line resets the playback
                if (tracks.isEmpty() || tracks.size == 1) {
                    tracks.addAll(queue)
                    queue.clear()
                }
            }
            player?.pause(null)
            player?.playUri(null, it.uri, 0, 0)
            playerView.update(currentTrack)
        }
    }

    fun pause() {
        player?.pause(null)
    }

    fun resume() {
        player?.resume(null)
    }

    fun toggleShuffle() {
        shuffle = !shuffle
    }

    /*
    Methods used to save presenter state from activity/fragment destruction, these methods take bundle as a parameter
    but its valid because the presenter itself doesn't depend on bundle and the presenter is still completely testable
    and even more separated from the UI component
     */

    //todo save queu too

    fun setState(bundle: Bundle?) {
        bundle?.getParcelableArrayList<Track>("tracks")?.let { tracks = LinkedList(it) }
        bundle?.getParcelable<Track>("currentTrack")?.let { currentTrack = it }
        bundle?.getBoolean("shuffle", shuffle)
    }

    fun saveState(bundle: Bundle) {
        bundle.putParcelableArrayList("tracks", ArrayList(tracks))
        bundle.putParcelable("currentTrack", currentTrack)
        bundle.putBoolean("shuffle", shuffle)
    }

}