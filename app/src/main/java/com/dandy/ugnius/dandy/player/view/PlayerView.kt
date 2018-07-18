package com.dandy.ugnius.dandy.player.view

import com.dandy.ugnius.dandy.model.entities.Track

interface PlayerView {
    fun update(track: Track)
    fun updateProgress()
    fun hasTrackEnded() : Boolean
    fun shouldRewind(): Boolean
    fun togglePlayButton(isPaused: Boolean)
    fun toggleShuffle(shuffle: Boolean)
    fun toggleReplay(replay: Boolean)
}