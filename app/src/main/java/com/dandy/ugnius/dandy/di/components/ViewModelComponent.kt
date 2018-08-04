package com.dandy.ugnius.dandy.di.components

import com.dandy.ugnius.dandy.artist.view.ArtistFragment
import com.dandy.ugnius.dandy.di.modules.GeneralModule
import com.dandy.ugnius.dandy.di.modules.ViewModelModule
import com.dandy.ugnius.dandy.likes.view.LikesFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ViewModelModule::class, GeneralModule::class])
interface ViewModelComponent {
    fun inject(likesFragment: LikesFragment)
    fun inject(artistFragment: ArtistFragment)

}