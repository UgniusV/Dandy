package com.dandy.ugnius.dandy.player.view

import com.dandy.ugnius.dandy.global.entities.Track

interface PlayerView {
    fun update(track: Track)
    fun updateProgress()
    fun hasTrackEnded() : Boolean
    fun shouldRewind(): Boolean
    fun togglePlayButton(isPaused: Boolean)
    fun toggleShuffle(isShuffle: Boolean)
    fun toggleReplay(isReplay: Boolean)
}