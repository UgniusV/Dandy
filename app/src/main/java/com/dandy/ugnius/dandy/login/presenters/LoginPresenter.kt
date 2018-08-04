package com.dandy.ugnius.dandy.login.presenters

import com.dandy.ugnius.dandy.global.clients.APIClient
import com.dandy.ugnius.dandy.global.entities.Album
import com.dandy.ugnius.dandy.login.model.clients.AuthenticationClient
import com.dandy.ugnius.dandy.global.repositories.Repository
import com.dandy.ugnius.dandy.login.views.fragments.LoginView
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function3
import io.reactivex.rxkotlin.subscribeBy
import java.util.concurrent.TimeUnit.SECONDS
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class LoginPresenter @Inject constructor(
    private val loginView: LoginView,
    private val authenticationClient: AuthenticationClient,
    private val apiClient: APIClient,
    private val repository: Repository
) {

    //Due to https://github.com/spotify/android-sdk/issues/408 we have to use a backend
    //server that is deployed on heroku and we have no control over it. Sometimes we get a Bad Gateway
    //error and we have to use exponential backoff
    private val retryCondition = { errors: Observable<Throwable> ->
        var delay = 1L
        val maximumAttempts = 3
        var attempts = 0
        errors.flatMap {
            if (attempts < maximumAttempts) {
                attempts += 1
                delay *= 2
                Observable.timer(delay, SECONDS)
            } else {
                Observable.error(it)
            }
        }
    }

    fun login(code: String) {
        authenticationClient.getCredentials(code)
            .retryWhen(retryCondition)
            .map { repository.insertCredentials(it) }
            .flatMapSingle { getUserLibraryQuery() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = { loginView.goToMainFragment() },
                onError = { it.message?.let { loginView.showError(it) } }
            )
    }

    //todo implement increasing offset queries
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
            Function3 { tracks, albums, artists ->
                with(repository) {
                    insertTracks(tracks)
                    insertAlbums(albums)
                    insertArtists(artists)
                }
            }
        )

    }
}