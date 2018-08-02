package com.dandy.ugnius.dandy.di.components

import com.dandy.ugnius.dandy.library.view.LibraryFragment
import com.dandy.ugnius.dandy.artist.view.ArtistFragment
import com.dandy.ugnius.dandy.di.modules.PersistanceModule
import com.dandy.ugnius.dandy.login.view.LoginFragment
import com.dandy.ugnius.dandy.main.MainActivity
import com.dandy.ugnius.dandy.player.receiver.StreamingNotificationReceiver
import com.dandy.ugnius.dandy.player.receivers.ConnectionStateReceiver
import com.dandy.ugnius.dandy.player.view.PlayerFragment
import com.dandy.ugnius.dandy.test.TestFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [PersistanceModule::class])
interface MainComponent {
    //todo kazka mastyt su components nes cia jau darosi messy
    fun inject(playerFragment: PlayerFragment)
    fun inject(streamingNotificationReceiver: StreamingNotificationReceiver)
    fun inject(artistFragment: ArtistFragment)
    fun inject(connectionStateReceiver: ConnectionStateReceiver)
    fun inject(mainActivity: MainActivity)
    fun inject(testFragment: TestFragment)
    fun inject(loginFragment: LoginFragment)
    fun inject(libraryFragment: LibraryFragment)
}