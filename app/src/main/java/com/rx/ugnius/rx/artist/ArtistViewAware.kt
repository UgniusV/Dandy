package com.rx.ugnius.rx.artist

import com.rx.ugnius.rx.api.entities.Album
import com.rx.ugnius.rx.api.entities.Artist
import com.rx.ugnius.rx.api.entities.Track

interface ArtistViewAware {
        fun displayArtistInfo(artist: Artist)
        fun displayArtistTopTracks(tracks: List<Track>)
        fun displayArtistAlbums(albums: Album)
        fun showError(message: String)
}