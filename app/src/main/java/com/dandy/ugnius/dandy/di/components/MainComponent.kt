package com.dandy.ugnius.dandy.di.components

import com.dandy.ugnius.dandy.artist.view.ArtistFragment
import com.dandy.ugnius.dandy.di.modules.NetworkModule
import com.dandy.ugnius.dandy.login.view.LoginActivity
import com.dandy.ugnius.dandy.main.MainActivity
import com.dandy.ugnius.dandy.player.receiver.StreamingNotificationReceiver
import com.dandy.ugnius.dandy.player.receivers.ConnectionStateReceiver
import com.dandy.ugnius.dandy.player.view.PlayerFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class])
interface MainComponent {
    fun inject(playerFragment: PlayerFragment)
    fun inject(streamingNotificationReceiver: StreamingNotificationReceiver)
    fun inject(artistFragment: ArtistFragment)
    fun inject(connectionStateReceiver: ConnectionStateReceiver)
    fun inject(mainActivity: LoginActivity)
}