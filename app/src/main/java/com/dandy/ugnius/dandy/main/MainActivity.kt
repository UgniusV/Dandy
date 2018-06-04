package com.dandy.ugnius.dandy.main

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.dandy.ugnius.dandy.R
import com.dandy.ugnius.dandy.artist.view.interfaces.ArtistFragmentDelegate
import kotlinx.android.synthetic.main.view_artist.*
import com.dandy.ugnius.dandy.player.view.PlayerFragment

class MainActivity : AppCompatActivity(), ArtistFragmentDelegate {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val bundle = Bundle()
        bundle.putString("artistId", "5IcR3N7QB1j6KBL8eImZ8m")
        val fragment = supportFragmentManager.findFragmentById(R.id.contentViewFragment)
        fragment.arguments = bundle

    }

    /*
       getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, firstFragment).commit();
     */

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


    override fun onArtistTrackClicked(trackId: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.contentViewFragment, PlayerFragment())
            .commit();
    }
}
