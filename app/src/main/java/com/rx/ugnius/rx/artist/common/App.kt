package com.rx.ugnius.rx.artist.common

import android.app.Application
import com.rx.ugnius.rx.artist.di.ArtistFragmentModule
import com.rx.ugnius.rx.artist.di.DaggerArtistFragmentComponent

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        val graph = DaggerArtistFragmentComponent.builder().artistFragmentModule(ArtistFragmentModule()).build()
    }
}