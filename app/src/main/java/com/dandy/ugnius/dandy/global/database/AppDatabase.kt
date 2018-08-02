package com.dandy.ugnius.dandy.global.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.dandy.ugnius.dandy.login.model.converters.StringListConverter
import com.dandy.ugnius.dandy.login.model.converters.TrackConverter
import com.dandy.ugnius.dandy.global.daos.AlbumsDao
import com.dandy.ugnius.dandy.global.daos.ArtistsDao
import com.dandy.ugnius.dandy.global.daos.CredentialsDao
import com.dandy.ugnius.dandy.global.daos.TracksDao
import com.dandy.ugnius.dandy.global.entities.Album
import com.dandy.ugnius.dandy.global.entities.Artist
import com.dandy.ugnius.dandy.global.entities.Credentials
import com.dandy.ugnius.dandy.global.entities.Track

@Database(entities = [Credentials::class, Track::class, Album::class, Artist::class], version = 1)
@TypeConverters(StringListConverter::class, TrackConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun credentialsDao(): CredentialsDao
    abstract fun tracksDao(): TracksDao
    abstract fun artistsDao(): ArtistsDao
    abstract fun albumsDao(): AlbumsDao

}