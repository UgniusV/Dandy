package com.rx.ugnius.rx.artist.model.entities.deserializers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.rx.ugnius.rx.artist.model.entities.Artist
import java.lang.reflect.Type

class ArtistDeserializer : JsonDeserializer<List<Artist>> {

    //TODO Refactor this
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): List<Artist> {
        val artists = arrayListOf<Artist>()
        if (json.isJsonArray) {
            json.asJsonArray.forEach {
                artists.add(context.deserialize(it, Artist::class.java))
            }
        } else {
            json.asJsonObject.getAsJsonArray("artists").forEach {
                artists.add(context.deserialize(it, Artist::class.java))
            }
        }
        return artists
    }

}