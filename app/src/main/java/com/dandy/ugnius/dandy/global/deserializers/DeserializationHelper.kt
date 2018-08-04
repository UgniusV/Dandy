package com.dandy.ugnius.dandy.global.deserializers

import com.google.gson.JsonObject

object DeserializationHelper {

    fun getImages(json: JsonObject): List<String> {
        val images = arrayListOf<String>()
        json.get("images").asJsonArray.forEach {
            images.add(it.asJsonObject.get("url").asString)
        }
        return images
    }
}