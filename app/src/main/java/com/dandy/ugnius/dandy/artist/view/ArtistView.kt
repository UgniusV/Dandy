package com.dandy.ugnius.dandy.artist.view

import com.dandy.ugnius.dandy.model.entities.Album
import com.dandy.ugnius.dandy.model.entities.Artist
import com.dandy.ugnius.dandy.model.entities.Track

interface ArtistView {
        fun setArtistInfo(artist: Artist)
        fun setArtistTracks(tracks: List<Track>)
        fun setArtistsAlbums(albums: List<Album>)
        fun setSimilarArtists(artists: List<Artist>)
        fun setAllTracks(allTracks: List<Track>)
        fun showError(message: String)
        fun showLoader()
        fun hideLoader()
}