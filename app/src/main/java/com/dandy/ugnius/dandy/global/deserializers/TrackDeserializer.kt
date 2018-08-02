package com.dandy.ugnius.dandy.global.deserializers

import android.text.format.DateUtils
import com.dandy.ugnius.dandy.global.entities.Track
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class TrackDeserializer : JsonDeserializer<Track> {

    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): Track {
        return with(json.asJsonObject) {
            val images = arrayListOf<String>()
            getAsJsonObject("album")?.getAsJsonArray("images")?.forEach {
                images.add(it.asJsonObject.get("url").asString)
            }
            val artists = arrayListOf<String>()
            getAsJsonArray("artists").forEach {
                artists.add(it.asJsonObject.get("name").asString)
            }
            Track(
                images,
                artists.joinToString(" & "),
                DateUtils.formatElapsedTime(get("duration_ms").asLong / 1000),
                get("explicit").asString == "true",
                get("id").asString,
                get("name").asString,
                get("uri").asString)
        }
    }

}

