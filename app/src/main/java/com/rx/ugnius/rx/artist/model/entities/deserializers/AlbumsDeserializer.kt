package com.rx.ugnius.rx.artist.model.entities.deserializers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.rx.ugnius.rx.artist.model.entities.Album
import java.lang.reflect.Type

class AlbumsDeserializer : JsonDeserializer<List<Album>> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): List<Album> {
        val albums = arrayListOf<Album>()
        json.asJsonObject.getAsJsonArray("items").forEach {
            albums.add(context.deserialize(it, Album::class.java))
        }
        return albums

    }
}