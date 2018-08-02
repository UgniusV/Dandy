package com.dandy.ugnius.dandy.main

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.graphics.Color.TRANSPARENT
import android.support.v4.app.FragmentTransaction
import com.dandy.ugnius.dandy.global.entities.Track
import com.dandy.ugnius.dandy.player.view.PlayerFragment
import kotlin.collections.ArrayList
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import com.dandy.ugnius.dandy.*
import com.dandy.ugnius.dandy.artist.view.ArtistFragment
import com.dandy.ugnius.dandy.di.components.DaggerMainComponent
import com.dandy.ugnius.dandy.di.modules.GeneralModule
import com.dandy.ugnius.dandy.global.entities.Album
import com.dandy.ugnius.dandy.global.repositories.Repository
import com.dandy.ugnius.dandy.login.events.UserCredentialsEnteredEvent
import com.dandy.ugnius.dandy.login.views.LoginFragment
import com.dandy.ugnius.dandy.login.views.LoginFragmentDelegate
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class MainActivity : AppCompatActivity(), LoginFragmentDelegate {

    @Inject lateinit var eventBus: EventBus
    @Inject lateinit var repository: Repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerMainComponent.builder()
            .generalModule(GeneralModule(this))
            .build()
            .inject(this)
        window.statusBarColor = TRANSPARENT
        window.decorView.systemUiVisibility = SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or SYSTEM_UI_FLAG_LAYOUT_STABLE
        setContentView(R.layout.activity_main)
        if (repository.getCredentials() == null) openLoginFragment() else openMainFragment()
    }

    private fun openLoginFragment() {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_vertical, R.anim.fade_out)
            .add(R.id.content, LoginFragment().apply { delegate = this@MainActivity })
            .commit()

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

    private fun openMainFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content, MainFragment())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    override fun onAuthenticationSuccess() {
        openMainFragment()
    }

}
