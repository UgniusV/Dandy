package com.dandy.ugnius.dandy.artist.view

import com.dandy.ugnius.dandy.artist.model.entities.Album
import com.dandy.ugnius.dandy.artist.model.entities.Artist
import com.dandy.ugnius.dandy.artist.model.entities.Track

interface ArtistView {
        fun setArtistInfo(artist: Artist)
        fun setArtistTracks(tracks: ArrayList<Track>)
        fun setArtistsAlbums(albums: List<Album>)
        fun setSimilarArtists(artists: List<Artist>)
        fun showError(message: String)
}