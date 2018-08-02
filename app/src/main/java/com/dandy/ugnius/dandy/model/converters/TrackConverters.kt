package com.dandy.ugnius.dandy.model.converters

import android.arch.persistence.room.TypeConverter
import com.dandy.ugnius.dandy.model.entities.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TrackConverters {

    @TypeConverter
    fun fromTracks(tracks: List<Track>?): String {
        val json = Gson().toJson(tracks)
        return json
    }

    @TypeConverter
    fun toTracks(track: String): List<Track>? {
        val listType = object : TypeToken<List<Track>>() {}.type
        return Gson().fromJson(track, listType)
    }
}
