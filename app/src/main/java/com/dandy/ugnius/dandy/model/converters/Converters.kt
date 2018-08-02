package com.dandy.ugnius.dandy.model.converters

import android.arch.persistence.room.TypeConverter
import com.dandy.ugnius.dandy.model.entities.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import java.util.ArrayList

class Converters {

    @TypeConverter
    fun fromString(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: List<String>): String {
        val json = Gson().toJson(list)
        return json
    }
}