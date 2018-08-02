package com.dandy.ugnius.dandy.model.daos

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import com.dandy.ugnius.dandy.model.entities.Artist
import com.dandy.ugnius.dandy.model.entities.Track

@Dao
interface ArtistsDao {

    @Query("SELECT * FROM artist")
    fun getAllArtists(): LiveData<List<Artist>>

    @Insert(onConflict = REPLACE)
    fun addAnArtist(artist: Artist)

    @Insert(onConflict = REPLACE)
    fun insertArtists(artists: List<Artist>)

    @Delete
    fun deleteAnArtist(artist: Artist)
}