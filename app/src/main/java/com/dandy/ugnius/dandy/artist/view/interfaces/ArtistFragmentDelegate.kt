package com.dandy.ugnius.dandy.artist.view.interfaces

import com.dandy.ugnius.dandy.artist.model.entities.Track

interface ArtistFragmentDelegate {
    fun onArtistTrackClicked(position: Int, tracks: ArrayList<Track>)
}