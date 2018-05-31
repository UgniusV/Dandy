package com.rx.ugnius.rx.artist.di

import com.rx.ugnius.rx.artist.view.ArtistFragment
import dagger.Component

@Component(modules = [ArtistFragmentModule::class])
interface ArtistFragmentComponent {
    fun inject(artistFragment: ArtistFragment)
}