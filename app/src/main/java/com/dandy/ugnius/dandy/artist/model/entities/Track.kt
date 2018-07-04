package com.dandy.ugnius.dandy.artist.model.entities

import com.google.gson.annotations.SerializedName
import com.dandy.ugnius.dandy.artist.model.entities.Album
import com.dandy.ugnius.dandy.artist.model.entities.Artist

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