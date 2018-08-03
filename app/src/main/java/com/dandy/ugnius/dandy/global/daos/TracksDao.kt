package com.dandy.ugnius.dandy.global.daos

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import com.dandy.ugnius.dandy.global.entities.Track

@Dao
interface TracksDao {

    @Query("SELECT * FROM track")
    fun getAllTracks(): LiveData<List<Track>>

    @Insert(onConflict = REPLACE)
    fun insertATrack(track: Track)

    @Insert(onConflict = REPLACE)
    fun insertTracks(tracks: List<Track>)

    @Delete
    fun deleteATrack(track: Track)

    @Delete
    fun deleteTracks(vararg track: Track)


}