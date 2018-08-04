package com.dandy.ugnius.dandy.global.deserializers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.dandy.ugnius.dandy.global.entities.Artist
import java.lang.reflect.Type

class ArtistsDeserializer : JsonDeserializer<List<Artist>> {

    //todo refactor deserializers
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): List<Artist> {
        val artists = arrayListOf<Artist>()
        if (json.isJsonArray) {
            json.asJsonArray.forEach {
                artists.add(context.deserialize(it, Artist::class.java))
            }
        } else {
            try {
                json.asJsonObject.getAsJsonObject("artists").getAsJsonArray("items").forEach {
                    artists.add(context.deserialize(it, Artist::class.java))
                }
            } catch (ex: ClassCastException) {
                json.asJsonObject.getAsJsonArray("artists").forEach {
                    artists.add(context.deserialize(it, Artist::class.java))
                }
            }
        }
        return artists
    }
}