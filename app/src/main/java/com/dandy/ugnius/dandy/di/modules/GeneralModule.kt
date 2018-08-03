package com.dandy.ugnius.dandy.di.modules

import android.arch.persistence.room.Room
import android.content.*
import dagger.Module
import android.support.v4.app.NotificationCompat
import com.App.Companion.CHANNEL_ID
import com.bumptech.glide.Glide
import com.dandy.ugnius.dandy.CLIENT_ID
import com.dandy.ugnius.dandy.global.database.AppDatabase
import com.dandy.ugnius.dandy.global.repositories.Repository
import com.spotify.sdk.android.player.Config
import com.spotify.sdk.android.player.Spotify
import com.spotify.sdk.android.player.SpotifyPlayer
import dagger.Provides
import org.greenrobot.eventbus.EventBus
import javax.inject.Singleton

@Module
class GeneralModule(private val context: Context) {

    @Provides
    fun provideSpotifyPlayer(repository: Repository): SpotifyPlayer {
        val accessToken = repository.getCredentials()?.accessToken
        val playerConfig = Config(context, accessToken, CLIENT_ID)
        return Spotify.getPlayer(playerConfig, context, null)

    }

    @Provides
    fun provideAppDatabase(): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "appDatabase-name")
            .allowMainThreadQueries()
            .build()
    }

    @Singleton
    @Provides
    fun provideNotificationBuilder() = NotificationCompat.Builder(context, CHANNEL_ID)

    @Singleton
    @Provides
    fun provideGlide() = Glide.with(context)

    @Singleton
    @Provides
    fun provideEventBus() = EventBus.getDefault()
}