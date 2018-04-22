package com.rx.ugnius.rx.api.entities

import com.google.gson.annotations.SerializedName

class Track(
        val album: Album,
        val artists: List<Artist>,
        @SerializedName("duration_ms")
        val duration: Int,
        val explicit: Boolean,
        val id: String,
        val isPlayable: Boolean,
        val name: String,
        val popularity: Int,
        val uri: String
)