package com.dandy.ugnius.dandy.di.modules

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import android.content.Context.MODE_PRIVATE
import android.support.v4.app.NotificationCompat
import com.App
import com.App.Companion.CHANNEL_ID
import com.bumptech.glide.Glide
import com.dandy.ugnius.dandy.CLIENT_ID
import com.spotify.sdk.android.player.Config
import com.spotify.sdk.android.player.Spotify
import com.spotify.sdk.android.player.SpotifyPlayer
import dagger.Provides
import javax.inject.Singleton

@Module
class UtilitiesModule(private val context: Context?) {

    @Singleton
    @Provides
    fun provideContext(): Context? = context

    @Singleton
    @Provides
    fun provideAuthenticationPreferences(context: Context?): SharedPreferences? {
        return context?.getSharedPreferences("authentication_preferences", MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideSpotifyPlayer(context: Context?, authenticationPreferences: SharedPreferences?): SpotifyPlayer {
        val accessToken = authenticationPreferences?.getString("access_token", "") ?: ""
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

    @Singleton
    @Provides
    fun provideNotificationBuilder(context: Context?) = NotificationCompat.Builder(context!!, CHANNEL_ID)

    @Singleton
    @Provides
    fun provideGlide(context: Context?) = Glide.with(context!!)
}