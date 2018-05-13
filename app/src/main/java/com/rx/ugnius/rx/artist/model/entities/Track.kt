package com.rx.ugnius.rx.artist.model.entities

import com.google.gson.annotations.SerializedName

class Track(
        var album: Album?,
        val artists: List<Artist>,
        @SerializedName("duration_ms")
        val duration: Long,
        val explicit: Boolean,
        val id: String,
        val isPlayable: Boolean,
        val name: String,
        val popularity: Int,
        val uri: String
)