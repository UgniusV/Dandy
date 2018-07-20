package com.dandy.ugnius.dandy.model.entities

import com.google.gson.annotations.Expose

class Album(
    val albumType: String,
    val artists: String,
    val id: String,
    var images: List<String>,
    val name: String,
    val releaseDate: String,
    @Expose
    var tracks: List<Track>?
) {
    override fun equals(other: Any?): Boolean {
        return if (other is Album) {
            albumType == other.albumType && name == other.name && releaseDate == other.releaseDate
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = 17
        result = 31 * result + albumType.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + releaseDate.hashCode()
        return result
    }
}