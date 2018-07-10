package com.dandy.ugnius.dandy.model.entities

import com.google.gson.annotations.Expose

class Album(
    val albumType: String,
    val artists: List<Artist>,
    val genres: List<String>,
    val id: String,
    var images: List<Image>,
    val name: String,
    val releaseDate: String,
    @Expose
    var tracks: List<Track>
)