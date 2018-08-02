package com.dandy.ugnius.dandy.model.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverter
import android.arch.persistence.room.TypeConverters
import com.dandy.ugnius.dandy.model.converters.Converters
import com.dandy.ugnius.dandy.model.converters.TrackConverters
import com.dandy.ugnius.dandy.model.daos.AlbumsDao
import com.dandy.ugnius.dandy.model.daos.ArtistsDao
import com.dandy.ugnius.dandy.model.daos.TracksDao
import com.dandy.ugnius.dandy.model.entities.Album
import com.dandy.ugnius.dandy.model.entities.Artist
import com.dandy.ugnius.dandy.model.entities.Track

@Database(entities = [Track::class, Album::class, Artist::class], version = 1)
@TypeConverters(Converters::class, TrackConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun tracksDao(): TracksDao
    abstract fun artistsDao(): ArtistsDao
    abstract fun albumsDao(): AlbumsDao

}