package com.rx.ugnius.rx.artist.view

import com.rx.ugnius.rx.artist.model.entities.Album
import com.rx.ugnius.rx.artist.model.entities.Artist
import com.rx.ugnius.rx.artist.model.entities.Track

interface ArtistView {
        fun setArtistInfo(artist: Artist)
        fun setArtistTracks(tracks: List<Track>)
        fun setArtistsAlbums(albums: List<Album>)
        fun setSimilarArtists(artists: List<Artist>)
        fun showError(message: String)
}