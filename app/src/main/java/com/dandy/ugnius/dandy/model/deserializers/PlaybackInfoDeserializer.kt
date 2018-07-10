package com.dandy.ugnius.dandy.model.deserializers

import com.dandy.ugnius.dandy.model.entities.PlaybackInfo
import com.dandy.ugnius.dandy.model.entities.Track
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class PlaybackInfoDeserializer : JsonDeserializer<PlaybackInfo> {

    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext): PlaybackInfo {
        println("flow: deserializing playback info")
        return with(json.asJsonObject) {
            val isShuffle = get("shuffle_state").asBoolean
            val isRepeat = get("repeat_state").asString != "off"
            val isPlaying = get("is_playing").asBoolean
            val track = context.deserialize<Track>(get("item"), Track::class.java)
            PlaybackInfo(isShuffle, isRepeat, isPlaying, track)
        }
    }

}