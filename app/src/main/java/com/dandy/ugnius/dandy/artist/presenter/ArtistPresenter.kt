package com.dandy.ugnius.dandy.artist.presenter

import com.dandy.ugnius.dandy.artist.view.ArtistView
import com.dandy.ugnius.dandy.artist.model.ArtistClient
import com.dandy.ugnius.dandy.artist.model.entities.Album
import com.dandy.ugnius.dandy.artist.model.entities.Artist
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toObservable
import java.util.*

class ArtistPresenter(private val artistClient: ArtistClient, private val artistsView: ArtistView) {

    private val compositeDisposable = CompositeDisposable()
    var allTracksSubscription: Disposable? = null

    fun queryArtist(artistId: String) {
        val disposable = artistClient.getArtist(artistId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onSuccess = { artistsView.setArtistInfo(it) },
                        onError = { it.message?.let { artistsView.showError(it) } }
                )
        compositeDisposable.add(disposable)
    }

    fun queryTopTracks(artistId: String, market: String) {
        val disposable = artistClient.getArtistTopTracks(artistId, market)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onSuccess = { artistsView.setArtistTracks(ArrayList(it)) },
                        onError = { it.message?.let { artistsView.showError(it) } }
                )
        compositeDisposable.add(disposable)
    }

    fun querySimilarArtists(artistId: String) {
        val disposable = artistClient.getArtistRelatedArtists(artistId)
                .flatMapObservable { it.toObservable() }
                .toSortedList { lhs: Artist, rhs: Artist ->
                    val lhsFollowers = lhs.followers.total
                    val rhsFollowers = rhs.followers.total
                    when {
                        rhsFollowers > lhsFollowers -> 1
                        rhsFollowers == lhsFollowers -> 0
                        else -> -1
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onSuccess = { artistsView.setSimilarArtists(it) },
                        onError = { it.message?.let { artistsView.showError(it) } }
                )
        compositeDisposable.add(disposable)
    }

    fun queryAlbums(artistId: String) {
        var album: Album? = null
        allTracksSubscription = artistClient.getArtistAlbums(artistId)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { artistsView.setArtistsAlbums(it) }
                .flatMapIterable { it }
                .concatMap {
                    album = it
                    artistClient.getAlbumsTracks(it.id)
                }
                .flatMapIterable { it }
                .map { it.also { it.album = album } }
                .toList()
                .cache()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        compositeDisposable.add(allTracksSubscription!!)
    }

    fun dispose() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

}