package com.dandy.ugnius.dandy.model.entities

import android.arch.persistence.room.*
import com.dandy.ugnius.dandy.model.converters.Converters
import com.google.gson.annotations.Expose

@Entity
class Album(
    var albumType: String,
    var artists: String,
    @PrimaryKey var id: String,
    var images: List<String>,
    var name: String,
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