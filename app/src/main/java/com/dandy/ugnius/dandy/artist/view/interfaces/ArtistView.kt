package com.dandy.ugnius.dandy.artist.view.interfaces

import com.dandy.ugnius.dandy.model.entities.Album
import com.dandy.ugnius.dandy.model.entities.Artist
import com.dandy.ugnius.dandy.model.entities.Track

interface ArtistView {
    fun setArtistInfo(artist: Artist)
    fun setSimilarArtists(similarArtists: List<Artist>)
    fun setAllTracksAndAlbums(tracks: List<Track>, albums: List<Album>)
    fun setArtistTopTracks(tracks: List<Track>)
    fun showError(message: String)
}