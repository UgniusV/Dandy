package com.dandy.ugnius.dandy.global.factories

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.dandy.ugnius.dandy.artist.presenter.ArtistViewModel
import com.dandy.ugnius.dandy.global.clients.APIClient
import com.dandy.ugnius.dandy.global.repositories.Repository
import com.dandy.ugnius.dandy.likes.viewmodels.LikesViewModel

class ViewModelFactory(
    private val apiClient: APIClient,
    private val repository: Repository
) : ViewModelProvider.Factory {

    //todo prevent unchecked cast
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LikesViewModel::class.java) -> LikesViewModel(repository) as T
            modelClass.isAssignableFrom(ArtistViewModel::class.java) -> ArtistViewModel(apiClient) as T
            else -> throw IllegalArgumentException("Provided ViewModel does not exist")
        }
    }

}