package com.dandy.ugnius.dandy.global.deserializers

import com.dandy.ugnius.dandy.global.entities.Artist
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.lang.reflect.Type

class ArtistDeserializer : JsonDeserializer<Artist> {

    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): Artist {
      return with(json.asJsonObject) {
          Artist(
              get("followers").asJsonObject.get("total").asLong,
              getGenres(this),
              get("id").asString,
              getImages(this),
              get("name").asString,
              get("popularity").asInt,
              null
          )
      }
    }

    private fun getGenres(json: JsonObject): String {
        val genres = arrayListOf<String>()
        json.get("genres").asJsonArray.forEach {
            genres.add(it.asString)
        }
        return genres.joinToString(" & ")
    }

    private fun getImages(json: JsonObject): List<String> {
        val images = arrayListOf<String>()
        json.get("images").asJsonArray.forEach {
            images.add(it.asJsonObject.get("url").asString)
        }
        return images
    }
}
