package com.dandy.ugnius.dandy.model.repositories

import android.arch.lifecycle.LiveData
import com.dandy.ugnius.dandy.model.database.AppDatabase
import com.dandy.ugnius.dandy.model.entities.Album
import com.dandy.ugnius.dandy.model.entities.Artist
import com.dandy.ugnius.dandy.model.entities.Track
import javax.inject.Inject

class Repository @Inject constructor(private val database: AppDatabase) {

    fun getTracks(): LiveData<List<Track>> {
        return database.tracksDao().getAllTracks()
    }

    fun getAlbums(): LiveData<List<Album>> {
        return database.albumsDao().getAllAlbums()
    }

    fun getArtists(): LiveData<List<Artist>> {
        return database.artistsDao().getAllArtists()
    }

    fun insertTracks(tracks: List<Track>) {
        database.tracksDao().insertTracks(tracks)
    }

    fun insertAlbums(albums: List<Album>) {
        database.albumsDao().insertAlbums(albums)
    }

    fun insertArtists(artists: List<Artist>) {
        database.artistsDao().insertArtists(artists)
    }

    fun deleteTrack(track: Track) {
        database.tracksDao().deleteATrack(track)
    }
}