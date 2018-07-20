package com.dandy.ugnius.dandy.artist.view

import com.dandy.ugnius.dandy.model.entities.Album
import com.dandy.ugnius.dandy.model.entities.Artist
import com.dandy.ugnius.dandy.model.entities.Track

interface ArtistView {
        fun setArtistInfo(artist: Artist)
        fun setSimilarArtists(artists: List<Artist>)
        fun setTracksAndAlbums(tracks: List<Track>, albums: List<Album>)
        fun showError(message: String)
}