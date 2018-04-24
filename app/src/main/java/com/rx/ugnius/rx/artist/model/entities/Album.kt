package com.rx.ugnius.rx.artist.model.entities

class Album(
        val albumType: String,
        val artists: List<Artist>,
        val genres: List<String>,
        val id: String,
        val images: List<Image>,
        val name: String,
        val releaseDate: String
)