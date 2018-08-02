package com.dandy.ugnius.dandy.library.view

import android.arch.lifecycle.ViewModel
import com.dandy.ugnius.dandy.model.clients.APIClient
import com.dandy.ugnius.dandy.model.entities.Track
import com.dandy.ugnius.dandy.model.repositories.Repository
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LibraryViewModel(
    private val apiClient: APIClient,
    private val repository: Repository
) : ViewModel() {

    private var disposable: Disposable? = null

    fun getTracks() = repository.getTracks()

    fun getAlbums() = repository.getAlbums()

    fun getArtists() = repository.getArtists()

    fun deleteTrack(track: Track) {
//        disposable = apiClient.deleteTrack(track.id)
//            .map {
//                repository.deleteTrack(track)
//            }
//            .subscribeBy(
//                onSuccess = {
//                    println("flow: success")
//                },
//                onError = {
//                    println("flow: error ${it.message}")
//                }
//            )
        apiClient.deleteTrack(track.id).enqueue(object : Callback<Unit> {
            override fun onFailure(call: Call<Unit>?, t: Throwable?) {
                println("flow: on error")
            }

            override fun onResponse(call: Call<Unit>?, response: Response<Unit>?) {
                println("flow: on response")
            }
        }
        )
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