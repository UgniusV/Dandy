package com.dandy.ugnius.dandy.di.modules

import com.dandy.ugnius.dandy.global.clients.APIClient
import com.dandy.ugnius.dandy.global.factories.ViewModelFactory
import com.dandy.ugnius.dandy.global.repositories.Repository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [NetworkModule::class])
class ViewModelModule {

    @Provides
    @Singleton
    fun provideViewModelFactory(apiClient: APIClient, repository: Repository): ViewModelFactory {
        return ViewModelFactory(apiClient, repository)
    }
}