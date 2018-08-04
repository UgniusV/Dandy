package com.dandy.ugnius.dandy.di.modules

import com.dandy.ugnius.dandy.player.view.PlayerView
import dagger.Module
import dagger.Provides

@Module(includes = [GeneralModule::class])
class PlayerModule(private val playerView: PlayerView) {

    @Provides
    fun providePlayerView() = playerView

}