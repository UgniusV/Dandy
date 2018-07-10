package com.dandy.ugnius.dandy.player.view

import com.dandy.ugnius.dandy.model.entities.PlaybackInfo
import com.dandy.ugnius.dandy.model.entities.Track
import com.spotify.sdk.android.player.Error

interface PlayerView {
    fun update(track: Track)
}