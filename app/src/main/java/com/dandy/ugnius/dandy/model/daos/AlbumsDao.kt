package com.dandy.ugnius.dandy.model.daos

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import com.dandy.ugnius.dandy.model.entities.Album

@Dao
interface AlbumsDao {

    @Query("SELECT * FROM album")
    fun getAllAlbums(): LiveData<List<Album>>

    @Insert(onConflict = REPLACE)
    fun insertAnAlbum(album: Album)

    @Insert(onConflict = REPLACE)
    fun insertAlbums(albums: List<Album>)

    @Delete
    fun deleteAnAlbum(album: Album)

    @Delete
    fun deleteAllAlbums(vararg albums: Album)
}