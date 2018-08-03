package com.dandy.ugnius.dandy.login.model.converters

import android.arch.persistence.room.TypeConverter
import com.dandy.ugnius.dandy.global.entities.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TrackConverter {

    @TypeConverter
    fun fromTracks(tracks: List<Track>?): String = Gson().toJson(tracks)

    @TypeConverter
    fun toTracks(track: String): List<Track>? {
        return Gson().fromJson(track, object : TypeToken<List<Track>>() {}.type)
    }

}