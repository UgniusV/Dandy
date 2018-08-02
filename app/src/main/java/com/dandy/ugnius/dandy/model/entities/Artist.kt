package com.dandy.ugnius.dandy.model.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.Expose

@Entity
class Artist(
    var followers: Long,
    var genres: String,
    @PrimaryKey var id: String,
    var images: List<String>,
    var name: String,
    var popularity: Int,
    @Expose
    var tracks: List<Track>?
)