package com.dandy.ugnius.dandy.player.view

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.os.Handler
import com.dandy.ugnius.dandy.model.entities.Track
import com.spotify.sdk.android.player.SpotifyPlayer
import java.util.*

class PlayerViewModel(private val player: SpotifyPlayer) : ViewModel() {

    lateinit var tracks: LinkedList<Track>
    private var queue = LinkedHashSet<Track>()
    private var shuffle: Boolean = false
    private var isPaused = false
    private lateinit var unmodifiableTracks: LinkedList<Track>
    private var replay: Boolean = false

    var currentTrack = MutableLiveData<Track>()

    fun playTrack() {
//        if (shuffle) {
//            queue.add(it)
//            tracks.remove(it)
//            if (tracks.isEmpty()) {
//                resetPlaybackQueue()
//            }
//        }
//        player.pause(null)
//        player.playUri(null, it.uri, 0, 0)
//        isPaused = false
    }

    private fun resetPlaybackQueue() {
        tracks = LinkedList(unmodifiableTracks)
        queue.clear()
    }


}