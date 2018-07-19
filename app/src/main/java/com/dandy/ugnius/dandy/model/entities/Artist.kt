package com.dandy.ugnius.dandy.model.entities

class Artist(
    val followers: Long,
    val genres: String,
    val id: String,
    val images: List<String>,
    val name: String,
    val popularity: Int
)