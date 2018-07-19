package com.dandy.ugnius.dandy.artist.presenter

import com.dandy.ugnius.dandy.artist.view.ArtistView
import com.dandy.ugnius.dandy.model.clients.APIClient
import com.dandy.ugnius.dandy.model.entities.Album
import com.dandy.ugnius.dandy.model.entities.Artist
import com.dandy.ugnius.dandy.model.entities.Track
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toObservable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ArtistPresenter(private val APIClient: APIClient, private val artistsView: ArtistView) {

    private val compositeDisposable = CompositeDisposable()
    private val format = SimpleDateFormat("yyyy-mm-dd", Locale.getDefault())

    fun query(artistId: String, market: String, groups: String) {
        queryArtist(artistId)
        queryRelatedArtist(artistId)
        queryTracksAndAlbums(artistId, groups, market)
    }

    private fun queryArtist(artistId: String) {
        val disposable = APIClient.getArtist(artistId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { artistsView.setArtistInfo(it) },
                onError = { it.message?.let { artistsView.showError(it) } }
            )
        compositeDisposable.add(disposable)
    }

    private fun queryRelatedArtist(artistId: String) {
        val disposable = APIClient.getArtistRelatedArtists(artistId)
            .flatMapObservable { it.toObservable() }
            .toSortedList { lhs: Artist, rhs: Artist ->
                when {
                    rhs.followers > lhs.followers -> 1
                    rhs.followers == lhs.followers -> 0
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


    /**
     * Queries artist top tracks and all of his albums and then assigns tracks to albums
     */

    private fun queryTracksAndAlbums(artistId: String, groups: String, market: String) {
        val disposable = APIClient.getArtistAlbums(artistId, groups)
            .flatMapIterable { it }
            .flatMap(
                { APIClient.getAlbumsTracks(it.id) },
                { album, tracks ->
                    tracks.forEach { it.images = album.images }
                    album.also { it.tracks = tracks }
                }
            )
            .toSortedList { lhs, rhs ->
                format.parse(rhs.releaseDate).compareTo(format.parse(lhs.releaseDate))
            }
            .observeOn(AndroidSchedulers.mainThread())
            .zipWith(
                APIClient.getArtistTopTracks(artistId, market),
                BiFunction { albums: List<Album>, topTracks: List<Track> ->
                    with(artistsView) {
                        val allTracks = LinkedHashSet<Track>(topTracks + albums.flatMap { it.tracks!! })
                        setAllTracks(ArrayList(allTracks))
                        setArtistTracks(ArrayList(topTracks))
                        setArtistsAlbums(albums)
                    }
                })
            .subscribeBy  { it.message?.let {
                artistsView.showError(it)
            }  }
        compositeDisposable.add(disposable)


    }

    fun dispose() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

}