package com.dandy.ugnius.dandy.player.di

import com.dandy.ugnius.dandy.player.view.PlayerFragment
import dagger.Component

@Component(modules = [PlayerModule::class])
interface PlayerComponent {
    fun inject(playerFragment: PlayerFragment)
}