package com.rx.ugnius.rx.api.entities

class Album(
        val albumType: String,
        val artists: List<Artist>,
        val genres: List<String>,
        val id: String,
        val images: List<Image>,
        val name: String,
        val releaseDate: String
)