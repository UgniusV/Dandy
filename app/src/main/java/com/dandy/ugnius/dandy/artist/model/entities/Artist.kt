package com.dandy.ugnius.dandy.artist.model.entities

class Artist(
        val followers: Followers,
        val genres: List<String>,
        val id: String,
        val images: List<Image>,
        val name: String,
        val popularity: Int
)