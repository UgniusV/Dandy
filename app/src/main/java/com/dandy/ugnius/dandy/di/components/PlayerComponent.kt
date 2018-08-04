package com.dandy.ugnius.dandy.di.components

import com.dandy.ugnius.dandy.di.modules.GeneralModule
import com.dandy.ugnius.dandy.di.modules.PlayerModule
import com.dandy.ugnius.dandy.player.view.PlayerFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [PlayerModule::class, GeneralModule::class])
interface PlayerComponent {
    fun inject(playerFragment: PlayerFragment)
}