package com.dandy.ugnius.dandy.player.view

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dandy.ugnius.dandy.R
import com.dandy.ugnius.dandy.artist.di.ArtistModule
import com.dandy.ugnius.dandy.player.di.DaggerPlayerComponent
import com.dandy.ugnius.dandy.player.di.PlayerModule
import com.dandy.ugnius.dandy.player.presenter.PlayerPresenter
import com.spotify.sdk.android.player.Player
import com.spotify.sdk.android.player.SpotifyPlayer
import javax.inject.Inject


class PlayerFragment : Fragment(), PlayerView {

    @Inject lateinit var playerPresenter: PlayerPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerPlayerComponent.builder()
            .playerModule(PlayerModule(this, context))
            .build()
            .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.view_player, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        arguments?.getString("trackId")?.let { playerPresenter.playTrack(it) }
    }

    override fun pause() {

    }

    override fun playNextSong() {

    }

    override fun playPreviousSong() {

    }

    override fun resume() {

    }

    override fun highlightShuffle() {

    }

    override fun highlightReplay() {

    }

    override fun highlightLibrary() {

    }
}
