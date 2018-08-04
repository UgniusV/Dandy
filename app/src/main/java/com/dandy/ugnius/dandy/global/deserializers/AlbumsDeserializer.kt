package com.dandy.ugnius.dandy.global.deserializers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.dandy.ugnius.dandy.global.entities.Album
import com.google.gson.JsonObject
import java.lang.reflect.Type

class AlbumsDeserializer : JsonDeserializer<List<Album>> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): List<Album> {
        val albums = arrayListOf<Album>()
        json.asJsonObject.getAsJsonArray("items").forEach {
            if (it.asJsonObject.get("added_at") == null) {
                albums.add(deserializeAlbum(it.asJsonObject))
            } else {
                albums.add(deserializeAlbum(it.asJsonObject.get("album").asJsonObject))
            }
        }
        return albums

    }

    private fun deserializeAlbum(json: JsonObject): Album {
        return with(json) {
            Album(
                get("album_type").asString,
                deserializeArtists(this),
                get("id").asString,
                DeserializationHelper.getImages(this),
                get("name").asString,
                deserializeDate(this),
                null
            )
        }
    }

    private fun deserializeDate(json: JsonObject): String {
        val date = json.get("release_date").asString
        return if (!date.contains("-")) {
            "$date-01-01"
        } else {
            date
        }
    }

    private fun deserializeArtists(json: JsonObject): String {
        val artists = arrayListOf<String>()
        json.get("artists").asJsonArray.forEach {
            artists.add(it.asJsonObject.get("name").asString)
        }
        return artists.joinToString(" & ")
    }

}