package com.dandy.ugnius.dandy.di.modules

import android.content.*
import dagger.Module
import android.content.Context.MODE_PRIVATE
import android.net.ConnectivityManager
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
class UtilitiesModule(private val context: Context) {

    @Provides
    fun provideContext(): Context = context

    @Provides
    fun provideAuthenticationPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("preferences", MODE_PRIVATE)
    }

    @Provides
    fun provideSpotifyPlayer(context: Context?, authenticationPreferences: SharedPreferences?): SpotifyPlayer {
        val accessToken = authenticationPreferences?.getString("accessToken", "") ?: ""
        val playerConfig = Config(context, accessToken, CLIENT_ID)
        return Spotify.getPlayer(playerConfig, context, null)

    }

    @Singleton
    @Provides
    fun provideNotificationBuilder(context: Context) = NotificationCompat.Builder(context, CHANNEL_ID)

    @Singleton
    @Provides
    fun provideGlide(context: Context) = Glide.with(context)
}