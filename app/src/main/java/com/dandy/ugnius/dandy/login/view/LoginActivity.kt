package com.dandy.ugnius.dandy.login.view

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.dandy.ugnius.dandy.*
import com.dandy.ugnius.dandy.main.MainActivity
import com.github.florent37.viewanimator.ViewAnimator
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse.Type.TOKEN
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
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
                val authenticationPreferences = getSharedPreferences("authentication_preferences", Context.MODE_PRIVATE)
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
