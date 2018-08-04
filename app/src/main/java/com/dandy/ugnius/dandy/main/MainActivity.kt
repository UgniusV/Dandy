package com.dandy.ugnius.dandy.main

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.view.View.VISIBLE
import android.view.View.GONE
import android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN
import com.dandy.ugnius.dandy.global.entities.Track
import com.dandy.ugnius.dandy.player.view.PlayerFragment
import kotlin.collections.ArrayList
import com.dandy.ugnius.dandy.*
import com.dandy.ugnius.dandy.artist.view.ArtistFragment
import com.dandy.ugnius.dandy.di.components.DaggerMainComponent
import com.dandy.ugnius.dandy.di.modules.GeneralModule
import com.dandy.ugnius.dandy.global.entities.Album
import com.dandy.ugnius.dandy.global.repositories.Repository
import com.dandy.ugnius.dandy.home.HomeFragment
import com.dandy.ugnius.dandy.likes.view.LikesFragment
import com.dandy.ugnius.dandy.login.events.UserCredentialsEnteredEvent
import com.dandy.ugnius.dandy.login.views.LoginFragment
import com.dandy.ugnius.dandy.login.views.LoginFragmentDelegate
import com.dandy.ugnius.dandy.playlists.PlaylistsFragment
import com.dandy.ugnius.dandy.search.SearchFragment
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class MainActivity : AppCompatActivity(), LoginFragmentDelegate {

    @Inject lateinit var eventBus: EventBus
    @Inject lateinit var repository: Repository

    private var loginFragment = LoginFragment().apply { delegate = this@MainActivity }
    private val homeFragment = HomeFragment()
    private val likesFragment = LikesFragment()
    private val searchFragment = SearchFragment()
    private val playlistsFragment = PlaylistsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerMainComponent.builder()
            .generalModule(GeneralModule(this))
            .build()
            .inject(this)

        setContentView(R.layout.activity_main)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> replace(homeFragment)
                R.id.likes -> replace(likesFragment)
                R.id.search -> replace(searchFragment)
                R.id.playlists -> replace(playlistsFragment)
                else -> throw IllegalArgumentException("Illegal item id")
            }
        }
        if (repository.getCredentials() == null) {
            add(loginFragment)
            bottomNavigation.visibility = GONE
        } else {
            bottomNavigation.selectedItemId = R.id.likes
        }
    }

    fun onArtistClicked(artistId: String) {
        val bundle = Bundle()
        bundle.putString("artistId", artistId)
        val fragment = ArtistFragment().apply { arguments = bundle }
        supportFragmentManager.beginTransaction()
            .setTransition(TRANSIT_FRAGMENT_OPEN)
            .replace(R.id.content, fragment)
            .addToBackStack(null)
            .commit()
        bottomNavigation.visibility = View.VISIBLE
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
            .commit()
    }

    override fun onLoginButtonPressed() {
        val builder = AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.CODE, REDIRECT_URI)
        builder.setScopes(Utilities.getScopes())
        val request = builder.build()
        AuthenticationClient.openLoginActivity(this, LOGIN_REQUEST_CODE, request)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == LOGIN_REQUEST_CODE) {
            val response = AuthenticationClient.getResponse(resultCode, intent)
            response.code?.let { eventBus.post(UserCredentialsEnteredEvent(it)) }
        }
    }

    override fun onAuthenticationSuccess() {
        remove(loginFragment)
        bottomNavigation.visibility = VISIBLE
        bottomNavigation.selectedItemId = R.id.likes
    }

    private fun replace(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content, fragment)
            .setTransition(TRANSIT_FRAGMENT_OPEN)
            .commit()
        return true
    }

    private fun add(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .add(R.id.content, fragment)
            .setTransition(TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    private fun remove(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .remove(fragment)
            .setTransition(TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

}
