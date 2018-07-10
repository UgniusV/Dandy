package com.dandy.ugnius.dandy.model.entities

import com.google.gson.annotations.SerializedName

class PlaybackInfo(
    @SerializedName("shuffle_state")
    val isShuffle: Boolean,
    @SerializedName("repeat_state")
    val isRepeat: Boolean,
    val isPlaying: Boolean,
    val track: Track
)