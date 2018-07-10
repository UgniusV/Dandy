package com.dandy.ugnius.dandy.artist.view.interfaces

import com.dandy.ugnius.dandy.model.entities.Track

interface ArtistFragmentDelegate {
    fun onArtistTrackClicked(currentTrackId: String, tracks: List<Track>)
}