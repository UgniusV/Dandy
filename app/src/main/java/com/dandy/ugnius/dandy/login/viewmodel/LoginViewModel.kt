package com.dandy.ugnius.dandy.login.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.SharedPreferences
import com.dandy.ugnius.dandy.login.model.Event
import com.dandy.ugnius.dandy.model.clients.APIClient
import com.dandy.ugnius.dandy.model.clients.AuthClient
import com.dandy.ugnius.dandy.model.entities.Album
import com.dandy.ugnius.dandy.model.entities.Artist
import com.dandy.ugnius.dandy.model.entities.Track
import com.dandy.ugnius.dandy.model.repositories.Repository
import io.reactivex.Single
import io.reactivex.functions.Function3
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class LoginViewModel(
    private val authClient: AuthClient,
    private val apiClient: APIClient,
    private val preferences: SharedPreferences,
    private val repository: Repository
) : ViewModel() {

    var event = MutableLiveData<Event>()

    fun login(code: String) {
        authClient.getCredentials(code)
            .map {
                preferences.edit().putString("accessToken", it.accessToken).apply()
                preferences.edit().putString("refreshToken", it.refreshToken).apply()
            }
            .flatMap { getUserLibraryQuery() }
            .subscribeBy(
                onSuccess = {
                    event.postValue(Event(isSuccessful = true, message = null))
                },
                onError = {
                    event.postValue(Event(isSuccessful = false, message = it.message))
                }
            )
    }

    private fun getUserLibraryQuery(): Single<Unit> {

        fun getSavedTracksQuery() = apiClient.getSavedTracks(0, 50).subscribeOn(Schedulers.io())

        fun getSavedAlbumsQuery(): Single<List<Album>> {
            return apiClient.getSavedAlbums()
                .flatMapIterable { it }
                .flatMap(
                    { apiClient.getAlbumsTracks(it.id) },
                    { album, tracks -> album.also { it.tracks = tracks } }
                )
                .toList()
                .subscribeOn(Schedulers.io())
        }

        fun getFollowingArtistsQuery() = apiClient.getFollowingArtists("artist").subscribeOn(Schedulers.io())

        return Single.zip(
            getSavedTracksQuery(),
            getSavedAlbumsQuery(),
            getFollowingArtistsQuery(),
            Function3 { tracks: List<Track>, albums: List<Album>, artist: List<Artist> ->
                with(repository) {
                    insertTracks(tracks)
                    insertAlbums(albums)
                    insertArtists(artist)

                }
            }
        )

    }
}