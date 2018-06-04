package player.view

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dandy.ugnius.dandy.R
import com.spotify.sdk.android.player.*
import com.spotify.sdk.android.authentication.LoginActivity.REQUEST_CODE
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse


class PlayerFragment : Fragment(), Player.NotificationCallback, ConnectionStateCallback {

    private val CLIENT_ID = "b6555c39f001444ab39401466d480010"
    private val REDIRECT_URI = "testschema://callback"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val builder = AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI)
        builder.setScopes(arrayOf("user-read-private", "streaming"))
        val request = builder.build()
        AuthenticationClient.openLoginActivity(activity, REQUEST_CODE, request)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.view_player, container, false)
    }

    //todo sitas neveikia ant fragmentu todel reikia iskelti login activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == REQUEST_CODE) {
            val response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.type == AuthenticationResponse.Type.TOKEN) {
                val playerConfig = Config(context, response.accessToken, CLIENT_ID);
                Spotify.getPlayer(playerConfig, this, object : SpotifyPlayer.InitializationObserver {
                    override fun onInitialized(player: SpotifyPlayer?) {
                        println("flow: initialized")
                        player?.playUri(null, "spotify:track:2TpxZ7JUBn3uw46aR7qd6V", 0, 0);
                    }

                    override fun onError(p0: Throwable?) {
                        println("flow: on error")
                    }
                })
            }
        }
    }


    override fun onPlaybackError(p0: Error?) {
        println("on playback error")
    }

    override fun onPlaybackEvent(p0: PlayerEvent?) {
        println("on playback event")
    }

    override fun onLoggedOut() {
        println("on logged out")
    }

    override fun onLoggedIn() {
        println("on logged in")
    }

    override fun onConnectionMessage(p0: String?) {
        println("onConnectionMessage")
    }

    override fun onLoginFailed(p0: Error?) {
        println("onLoginFailed")
    }

    override fun onTemporaryError() {
        println("onTemporaryError")
    }
}
