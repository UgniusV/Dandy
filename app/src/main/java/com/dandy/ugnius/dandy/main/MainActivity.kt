package com.dandy.ugnius.dandy.main

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.graphics.Color.TRANSPARENT
import android.support.v4.app.FragmentManager
import android.view.MenuItem
import com.dandy.ugnius.dandy.R
import com.dandy.ugnius.dandy.model.entities.Track
import kotlinx.android.synthetic.main.view_artist.*
import com.dandy.ugnius.dandy.player.view.PlayerFragment
import kotlin.collections.ArrayList
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.view.WindowManager
import com.dandy.ugnius.dandy.artist.view.ArtistFragment
import com.dandy.ugnius.dandy.model.entities.Album
import com.dandy.ugnius.dandy.test.TestFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = TRANSPARENT
        window.decorView.systemUiVisibility = SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or SYSTEM_UI_FLAG_LAYOUT_STABLE
        setContentView(R.layout.activity_main)
        onArtistClicked("2rhFzFmezpnW82MNqEKVry")
    }

    fun onArtistClicked(artistId: String) {
        val bundle = Bundle()
        bundle.putString("artistId", artistId)
        val fragment = ArtistFragment().apply { arguments = bundle }
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_vertical, R.anim.fade_out)
            .replace(R.id.content, fragment)
            .addToBackStack(null)
            .commit()
    }

    fun onAlbumClicked(album: Album) {
        //todo open album fragment
    }

    fun onArtistTrackClicked(currentTrack: Track, tracks: List<Track>) {
        val bundle = Bundle().apply {
            putParcelable("currentTrack", currentTrack)
            putParcelableArrayList("tracks", ArrayList(tracks))
        }
        val playerFragment = PlayerFragment().apply { arguments = bundle }
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_vertical, R.anim.fade_out)
            .replace(R.id.content, playerFragment)
            .addToBackStack(null)
            .commit();
    }
}
