package com.dandy.ugnius.dandy.main.view

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View.VISIBLE
import android.view.View.GONE
import android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN
import com.dandy.ugnius.dandy.global.entities.Track
import com.dandy.ugnius.dandy.player.view.PlayerFragment
import kotlin.collections.ArrayList
import com.dandy.ugnius.dandy.*
import com.dandy.ugnius.dandy.artist.view.fragments.ArtistFragment
import com.dandy.ugnius.dandy.artist.view.adapters.AlbumsAdapterDelegate
import com.dandy.ugnius.dandy.artist.view.adapters.ArtistsAdapterDelegate
import com.dandy.ugnius.dandy.artist.view.adapters.TracksAdapterDelegate
import com.dandy.ugnius.dandy.di.components.DaggerMainComponent
import com.dandy.ugnius.dandy.di.modules.GeneralModule
import com.dandy.ugnius.dandy.global.entities.Album
import com.dandy.ugnius.dandy.global.entities.Artist
import com.dandy.ugnius.dandy.global.repositories.Repository
import com.dandy.ugnius.dandy.home.view.fragments.HomeFragment
import com.dandy.ugnius.dandy.likes.view.fragments.LikesFragment
import com.dandy.ugnius.dandy.login.events.UserCredentialsEnteredEvent
import com.dandy.ugnius.dandy.login.views.fragments.LoginFragment
import com.dandy.ugnius.dandy.login.views.fragments.LoginFragmentDelegate
import com.dandy.ugnius.dandy.playlists.PlaylistsFragment
import com.dandy.ugnius.dandy.search.SearchFragment
import com.spotify.sdk.android.authentication.AuthenticationResponse.Type.CODE
import com.dandy.ugnius.dandy.utilities.CLIENT_ID
import com.dandy.ugnius.dandy.utilities.LOGIN_REQUEST_CODE
import com.dandy.ugnius.dandy.utilities.REDIRECT_URI
import com.dandy.ugnius.dandy.utilities.Utilities
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class MainActivity : AppCompatActivity(),
    LoginFragmentDelegate,
    TracksAdapterDelegate,
    AlbumsAdapterDelegate,
    ArtistsAdapterDelegate
{

    @Inject lateinit var eventBus: EventBus
    @Inject lateinit var repository: Repository

    private val playerFragment = PlayerFragment()
    private val loginFragment = LoginFragment().apply { delegate = this@MainActivity }
    private val homeFragment = HomeFragment().apply {
        tracksAdapterDelegate = this@MainActivity
        artistsAdapterDelegate = this@MainActivity
    }
    private val likesFragment = LikesFragment().apply {
        tracksAdapterDelegate = this@MainActivity
        albumsAdapterDelegate = this@MainActivity
    }
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
                R.id.home -> navigate(homeFragment)
                R.id.likes -> navigate(likesFragment)
                R.id.search -> navigate(searchFragment)
                R.id.playlists -> navigate(playlistsFragment)
                else -> throw IllegalArgumentException("Illegal item id")
            }
        }
        if (repository.getCredentials() == null) {
            navigate(loginFragment)
            bottomNavigation.visibility = GONE
        } else {
            bottomNavigation.selectedItemId = R.id.home
        }
    }

    override fun onAlbumClicked(album: Album) {
        //todo open album fragment
    }

    override fun onLoginButtonPressed() {
        val builder = AuthenticationRequest.Builder(CLIENT_ID, CODE, REDIRECT_URI)
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
        bottomNavigation.visibility = VISIBLE
        bottomNavigation.selectedItemId = R.id.home
    }

    private fun navigate(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content, fragment)
            .setTransition(TRANSIT_FRAGMENT_OPEN)
            .commit()
        return true
    }

    override fun onArtistClicked(artist: Artist) {
        val bundle = Bundle()
        bundle.putString("artistId", artist.id)
        val artistFragment = ArtistFragment().apply {
            arguments = bundle
            tracksAdapterDelegate = this@MainActivity
            albumsAdapterDelegate = this@MainActivity
            artistsAdapterDelegate = this@MainActivity
        }
        supportFragmentManager.beginTransaction()
            .setTransition(TRANSIT_FRAGMENT_OPEN)
            .replace(R.id.content, artistFragment)
            .addToBackStack(null)
            .commit()
        bottomNavigation.visibility = VISIBLE
    }

    override fun onTrackClicked(currentTrack: Track, allTracks: List<Track>) {
        val bundle = Bundle().apply {
            putParcelable("currentTrack", currentTrack)
            putParcelableArrayList("tracks", ArrayList(allTracks))
        }
        val playerFragment = playerFragment.apply { arguments = bundle }
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_vertical, R.anim.fade_out)
            .replace(R.id.content, playerFragment.apply { arguments = bundle })
            .addToBackStack(null)
            .commit()
    }
}
