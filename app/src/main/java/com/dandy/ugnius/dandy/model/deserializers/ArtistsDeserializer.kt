package com.dandy.ugnius.dandy.model.deserializers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.dandy.ugnius.dandy.model.entities.Artist
import java.lang.reflect.Type

class ArtistsDeserializer : JsonDeserializer<List<Artist>> {

    //Merge operator
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): List<Artist> {
        val artists = arrayListOf<Artist>()
        if (json.isJsonArray) {
            json.asJsonArray.forEach {
                artists.add(context.deserialize(it, Artist::class.java))
            }
        } else {
            if (json.asJsonObject.getAsJsonObject("artists") == null) {
                json.asJsonObject.getAsJsonArray("artists").forEach {
                    artists.add(context.deserialize(it, Artist::class.java))
                }
            } else {
                json.asJsonObject.getAsJsonObject("artists").getAsJsonArray("items").forEach {
                    artists.add(context.deserialize(it, Artist::class.java))
                }
            }
        }
        return artists
    }

}