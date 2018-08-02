package com.dandy.ugnius.dandy.main

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.graphics.Color.TRANSPARENT
import android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE
import com.dandy.ugnius.dandy.model.entities.Track
import com.dandy.ugnius.dandy.player.view.PlayerFragment
import kotlin.collections.ArrayList
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import com.dandy.ugnius.dandy.*
import com.dandy.ugnius.dandy.artist.view.ArtistFragment
import com.dandy.ugnius.dandy.library.view.LibraryFragment
import com.dandy.ugnius.dandy.login.view.LoginFragment
import com.dandy.ugnius.dandy.model.entities.Album
import com.dandy.ugnius.dandy.test.TestFragment
import com.spotify.sdk.android.authentication.AuthenticationClient

class MainActivity : AppCompatActivity() {

    //todo paziureti ar tokiu budu nebus memory leaku nes kazkaip sketchy laikyt fragment instanca
    private val loginFragment = LoginFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = TRANSPARENT
        window.decorView.systemUiVisibility = SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or SYSTEM_UI_FLAG_LAYOUT_STABLE
        setContentView(R.layout.activity_main)
        openTestFragment()
//        openLoginFragment()
//        openLibraryFragment()
//        onArtistClicked("2rhFzFmezplpnW82MNqEKVry")
//        onArtistClicked("6fxyWrfmjcbj5d12gXeiNV")
    }

    private fun openTestFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.content, TestFragment())
            .setTransition(TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    private fun openLoginFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.content, loginFragment)
            .setTransition(TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    fun openLibraryFragment() {
        onArtistClicked("2rhFzFmezplpnW82MNqEKVry")
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.content, LibraryFragment())
//            .setTransition(TRANSIT_FRAGMENT_FADE)
//            .commit()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == LOGIN_REQUEST_CODE) {
            val response = AuthenticationClient.getResponse(resultCode, intent)
            loginFragment.login(response.code)
//            login(response.code)
        }
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
