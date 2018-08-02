package com.dandy.ugnius.dandy.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton
import com.dandy.ugnius.dandy.model.database.AppDatabase
import android.arch.persistence.room.Room
import android.content.SharedPreferences
import com.dandy.ugnius.dandy.login.viewmodel.ViewModelFactory
import com.dandy.ugnius.dandy.model.clients.APIClient
import com.dandy.ugnius.dandy.model.clients.AuthClient
import com.dandy.ugnius.dandy.model.repositories.Repository

@Module(includes = [NetworkModule::class])
class PersistanceModule(val context: Context) {

    @Singleton
    @Provides
    fun provideAppDatabase(): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "appDatabase-name").build()
    }

    @Singleton
    @Provides
    fun provideViewModelFactory(
        authClient: AuthClient,
        apiClient: APIClient,
        preferences: SharedPreferences,
        repository: Repository
    ): ViewModelFactory {
        return ViewModelFactory(authClient, apiClient, preferences, repository)
    }

}