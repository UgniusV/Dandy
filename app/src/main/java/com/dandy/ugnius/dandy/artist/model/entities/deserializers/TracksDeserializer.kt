package com.dandy.ugnius.dandy.artist.model.entities.deserializers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.dandy.ugnius.dandy.artist.model.entities.Track
import com.google.gson.JsonObject
import java.lang.reflect.Type

class TracksDeserializer : JsonDeserializer<List<Track>> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): List<Track> {
        val tracks = arrayListOf<Track>()
        val jsonObject = json.asJsonObject
        val entries = jsonObject.getAsJsonArray("tracks") ?: jsonObject.getAsJsonArray("items")
        entries.forEach {
            tracks.add(createTrack(it.asJsonObject))
        }
        return tracks
    }

    private fun createTrack(json: JsonObject): Track {
        val images = arrayListOf<String>()
        json.getAsJsonObject("album")?.getAsJsonArray("images")?.forEach {
            images.add(it.asJsonObject.get("url").asString)
        }
        val artists = arrayListOf<String>()
        json.getAsJsonArray("artists").forEach {
            artists.add(it.asJsonObject.get("name").asString)
        }

        return Track(
            images,
            artists.joinToString(" & "),
            json.get("duration_ms").asLong,
            json.get("explicit").asBoolean,
            json.get("id").asString,
            json.get("name").asString,
            json.get("uri").asString)
    }


}