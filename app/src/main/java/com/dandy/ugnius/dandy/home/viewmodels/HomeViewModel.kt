package com.dandy.ugnius.dandy.home.viewmodels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.dandy.ugnius.dandy.global.clients.APIClient
import com.dandy.ugnius.dandy.global.entities.Artist
import com.dandy.ugnius.dandy.global.entities.Error
import com.dandy.ugnius.dandy.global.entities.Track
import com.dandy.ugnius.dandy.home.view.adapters.HomeAdapter
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class HomeViewModel(private val apiClient: APIClient) : ViewModel() {

    private var disposable: Disposable? = null
    var entry = MutableLiveData<HomeAdapter.HomeAdapterEntry>()
    var error = MutableLiveData<Error>()

    fun getEntries() {
        disposable = Single.zip(
            apiClient.getMyTopTracks().subscribeOn(Schedulers.io()),
            apiClient.getMyTopArtists().subscribeOn(Schedulers.io()),
            BiFunction { tracks: List<Track>, artists: List<Artist> ->
                entry.postValue(HomeAdapter.HomeAdapterEntry(tracks, artists))
            }
        ).subscribeBy(onError = { it.message?.let { error.postValue(Error(it)) } })
    }

    override fun onCleared() {
        super.onCleared()
        disposable?.let {
            if (!it.isDisposed) {
                it.dispose()
            }
        }
    }
}