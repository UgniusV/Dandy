package com.dandy.ugnius.dandy.main

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.graphics.Color.TRANSPARENT
import android.view.MenuItem
import com.dandy.ugnius.dandy.R
import com.dandy.ugnius.dandy.model.entities.Track
import com.dandy.ugnius.dandy.artist.view.interfaces.ArtistFragmentDelegate
import com.dandy.ugnius.dandy.events.AllArtistTracksQueriedEvent
import kotlinx.android.synthetic.main.view_artist.*
import com.dandy.ugnius.dandy.player.view.PlayerFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.collections.ArrayList
import android.view.WindowManager
import android.os.Build
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE


class MainActivity : AppCompatActivity(), ArtistFragmentDelegate {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = TRANSPARENT
        window.decorView.systemUiVisibility = SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or SYSTEM_UI_FLAG_LAYOUT_STABLE
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val bundle = Bundle()
        bundle.putString("artistId", "5IcR3N7QB1j6KBL8eImZ8m")
        val fragment = supportFragmentManager.findFragmentById(R.id.contentViewFragment)
        fragment.arguments = bundle

    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: AllArtistTracksQueriedEvent) {
        event.allTracks
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.artist_toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == R.id.actionFavorite) {
            println("favorite was clicked")
            true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }

    //todo this wont play all the tracks correctly after top ten ends
    override fun onArtistTrackClicked(currentTrackId: String, tracks: List<Track>) {
        val bundle = Bundle().apply {
            putString("currentTrackId", currentTrackId)
            putParcelableArrayList("tracks", ArrayList(tracks))
        }
        val playerFragment = PlayerFragment().apply { arguments = bundle }
        supportFragmentManager.beginTransaction()
            .replace(R.id.contentViewFragment, playerFragment)
            .addToBackStack(null)
            .commit();
    }
}
