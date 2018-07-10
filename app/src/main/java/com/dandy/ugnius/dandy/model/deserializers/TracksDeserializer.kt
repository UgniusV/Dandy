package com.dandy.ugnius.dandy.model.deserializers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.dandy.ugnius.dandy.model.entities.Track
import java.lang.reflect.Type

class TracksDeserializer : JsonDeserializer<List<Track>> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): List<Track> {
        val tracks = arrayListOf<Track>()
        val jsonObject = json.asJsonObject
        val entries = jsonObject.getAsJsonArray("tracks") ?: jsonObject.getAsJsonArray("items")
        entries.forEach {
            tracks.add(context.deserialize(it, Track::class.java))
        }
        return tracks
    }

}