package com.rx.ugnius.rx.artist

import com.rx.ugnius.rx.artist.model.entities.Album
import com.rx.ugnius.rx.artist.model.entities.Artist
import com.rx.ugnius.rx.artist.model.entities.Track

interface ArtistScreen {
        fun displayArtistInfo(artist: Artist)
        fun displayArtistTopTracks(tracks: List<Track>)
        fun displayArtistAlbums(albums: List<Album>)
        fun showError(message: String)
}