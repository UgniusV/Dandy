package com.dandy.ugnius.dandy.di.components

import com.dandy.ugnius.dandy.di.modules.GeneralModule
import com.dandy.ugnius.dandy.main.view.MainActivity
import com.dandy.ugnius.dandy.player.receivers.StreamingNotificationReceiver
import com.dandy.ugnius.dandy.player.receivers.ConnectionStateReceiver
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [GeneralModule::class])
interface MainComponent {
    fun inject(streamingNotificationReceiver: StreamingNotificationReceiver)
    fun inject(connectionStateReceiver: ConnectionStateReceiver)
    fun inject(mainActivity: MainActivity)
}