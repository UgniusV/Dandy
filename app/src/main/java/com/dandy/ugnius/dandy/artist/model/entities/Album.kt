package com.dandy.ugnius.dandy.artist.model.entities

class Album(
        val albumType: String,
        val artists: List<Artist>,
        val genres: List<String>,
        val id: String,
        var images: List<Image>,
        val name: String,
        val releaseDate: String
)