package com.dandy.ugnius.dandy.player.view

interface PlayerView {
    fun pause()
    fun playNextSong()
    fun playPreviousSong()
    fun resume()
    fun highlightShuffle()
    fun highlightReplay()
    fun highlightLibrary()
}