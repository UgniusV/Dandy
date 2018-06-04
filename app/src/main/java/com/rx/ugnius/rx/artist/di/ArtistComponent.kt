package com.rx.ugnius.rx.artist.di

import com.rx.ugnius.rx.artist.view.ArtistFragment
import dagger.Component

@Component(modules = [ArtistModule::class])
//todo jeigu musu component dependina nuo kito component mes galime daryti
//@Component(modules = [ArtistModule::class], dependencies = [OtherComponent::class])
interface ArtistComponent {
    fun inject(artistFragment: ArtistFragment)
}