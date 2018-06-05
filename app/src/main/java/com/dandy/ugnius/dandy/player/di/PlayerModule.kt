package com.dandy.ugnius.dandy.player.di

import android.content.Context
import android.content.SharedPreferences
import com.dandy.ugnius.dandy.CLIENT_ID
import com.dandy.ugnius.dandy.player.view.PlayerView
import com.spotify.sdk.android.player.Config
import com.spotify.sdk.android.player.Spotify
import com.spotify.sdk.android.player.SpotifyPlayer
import dagger.Module
import dagger.Provides

@Module
class PlayerModule(private val playerView: PlayerView, private val context: Context?) {

    @Provides
    fun provideSpotifyPlayer(authenticationPreferences: SharedPreferences?): SpotifyPlayer{
        val accessToken = authenticationPreferences?.getString("access_token","") ?: ""
        val playerConfig = Config(context, accessToken, CLIENT_ID)
        return Spotify.getPlayer(playerConfig, this, object : SpotifyPlayer.InitializationObserver {
            override fun onInitialized(p0: SpotifyPlayer?) {
                println("player was initialized")
            }

            override fun onError(p0: Throwable?) {
                //eventBus -> post player failed to initialized event
                println("on error")
            }
        })

    }

    @Provides
    fun providePlayerView() = playerView

    //todo provideAuthenticationPreferences gal kazkaip iseina iskelt nes jis bus naudojamas daug vietu
    @Provides
    fun provideAuthenticationPreferences(): SharedPreferences? {
        return context?.getSharedPreferences("authentication_preferences", Context.MODE_PRIVATE)
    }


    /*
     Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
            Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                @Override
                public void onInitialized(SpotifyPlayer spotifyPlayer) {
                    mPlayer = spotifyPlayer;
                    mPlayer.addConnectionStateCallback(MainActivity.this);
                    mPlayer.addNotificationCallback(MainActivity.this);
                }

                @Override
                public void onError(Throwable throwable) {
                    Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                }
            });
     */

}