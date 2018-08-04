package com.dandy.ugnius.dandy.likes.viewmodels

import android.arch.lifecycle.ViewModel
import com.dandy.ugnius.dandy.global.repositories.Repository

class LikesViewModel(private val repository: Repository) : ViewModel() {

    fun getTracks() = repository.getTracks()
    fun getAlbums() = repository.getAlbums()

}