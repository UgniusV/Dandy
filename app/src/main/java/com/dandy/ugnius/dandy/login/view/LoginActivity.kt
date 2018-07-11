package com.dandy.ugnius.dandy.login.view

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import com.dandy.ugnius.dandy.*
import com.dandy.ugnius.dandy.main.MainActivity
import com.github.florent37.viewanimator.ViewAnimator
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse.Type.TOKEN
import kotlinx.android.synthetic.main.activity_login.*
import android.app.PendingIntent
import android.media.session.MediaSession
import android.provider.Settings
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.media.session.MediaSessionCompat
import com.App
import com.spotify.sdk.android.player.SpotifyPlayer
import java.security.AccessController.getContext
import javax.inject.Inject


class LoginActivity : AppCompatActivity() {

    @Inject lateinit var player: SpotifyPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        (applicationContext as App).mainComponent?.inject(this)
        dandyAnimation.addAnimatorUpdateListener {
            if (it.animatedFraction == 1F) {
                dandyAnimation.reverseAnimationSpeed()
            }
        }
        ViewAnimator.animate(dandy)
            .translationY(-300F, 0F)
            .alpha(0F, 1F)
            .startDelay(1000)
            .duration(400)
            .thenAnimate(description)
            .translationY(-300F, 0F)
            .alpha(0F, 1F)
            .duration(400)
            .andAnimate(loginButton)
            .alpha(0F, 1F)
            .duration(400)
            .onStop { loginButton.setOnClickListener { login() } }
            .start()
    }

    private fun login() {
        val builder = AuthenticationRequest.Builder(CLIENT_ID, TOKEN, REDIRECT_URI)
        builder.setScopes(Utilities.getScopes())
        val request = builder.build()
        AuthenticationClient.openLoginActivity(this, LOGIN_REQUEST_CODE, request)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == LOGIN_REQUEST_CODE) {
            val response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.type == TOKEN) {
                val authenticationPreferences = getSharedPreferences("preferences", Context.MODE_PRIVATE)
                authenticationPreferences?.edit()?.putString("access_token", response.accessToken)?.apply()
                goToMainActivity()
            }
        }
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

}
