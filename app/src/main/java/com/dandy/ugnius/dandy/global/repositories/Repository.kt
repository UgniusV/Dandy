package com.dandy.ugnius.dandy.global.repositories

import com.dandy.ugnius.dandy.global.database.AppDatabase
import com.dandy.ugnius.dandy.global.entities.Album
import com.dandy.ugnius.dandy.global.entities.Artist
import com.dandy.ugnius.dandy.global.entities.Credentials
import com.dandy.ugnius.dandy.global.entities.Track
import javax.inject.Inject

class Repository @Inject constructor(private val database: AppDatabase) {

    fun insertCredentials(credentials: Credentials) = database.credentialsDao().insertCredentials(credentials)

    //will be used later
    fun updateCredentials(credentials: Credentials) = database.credentialsDao().updateCredentials(credentials)

    fun getCredentials() = database.credentialsDao().getCredentials()

    fun getTracks() = database.tracksDao().getAllTracks()

    fun getAlbums() = database.albumsDao().getAllAlbums()

    //will be used later
    fun getArtists() = database.artistsDao().getAllArtists()

    fun insertTracks(tracks: List<Track>) = database.tracksDao().insertTracks(tracks)

    fun insertAlbums(albums: List<Album>) = database.albumsDao().insertAlbums(albums)

    fun insertArtists(artists: List<Artist>) = database.artistsDao().insertArtists(artists)

    //will be used later
    fun deleteTrack(track: Track) = database.tracksDao().deleteATrack(track)
}