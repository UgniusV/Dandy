package com.dandy.ugnius.dandy.model.entities

import com.google.gson.annotations.Expose

class Album(
    val albumType: String?,
    val artists: String,
    val id: String,
    var images: List<String>,
    val name: String,
    val releaseDate: String,
    @Expose
    var tracks: List<Track>?
)