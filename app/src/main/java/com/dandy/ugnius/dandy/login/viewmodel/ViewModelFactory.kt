package com.dandy.ugnius.dandy.login.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.SharedPreferences
import com.dandy.ugnius.dandy.artist.presenter.ArtistViewModel
import com.dandy.ugnius.dandy.library.view.LibraryViewModel
import com.dandy.ugnius.dandy.model.clients.APIClient
import com.dandy.ugnius.dandy.model.clients.AuthClient
import com.dandy.ugnius.dandy.model.repositories.Repository

class ViewModelFactory(
    private val authClient: AuthClient,
    private val apiClient: APIClient,
    private val preferences: SharedPreferences,
    private val repository: Repository
) : ViewModelProvider.Factory {

    //todo paziureti kodel cia unchecked castai
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
       return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(authClient, apiClient, preferences, repository) as T
            }
            modelClass.isAssignableFrom(LibraryViewModel::class.java) -> {
                LibraryViewModel(apiClient, repository) as T
            }
           modelClass.isAssignableFrom(ArtistViewModel::class.java) -> {
               ArtistViewModel(apiClient) as T
           }
            else ->  throw IllegalArgumentException("View model not found")
        }
    }

}