package com.rx.ugnius.rx.artist.presenter

import com.rx.ugnius.rx.artist.model.ArtistClient
import com.rx.ugnius.rx.artist.model.RetrofitConfigurator
import com.rx.ugnius.rx.artist.model.entities.Album
import com.rx.ugnius.rx.artist.model.entities.Track
import com.rx.ugnius.rx.artist.view.View
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toObservable
import java.util.*

class ArtistPresenter(private val artistsView: View) {

    private val artistClient = RetrofitConfigurator.configure().create(ArtistClient::class.java)

    fun queryArtist(artistId: String) {
        artistClient.getArtist(artistId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onSuccess = { artistsView.displayArtistInfo(it) },
                        onError = { it.message?.let { artistsView.showError(it) } }
                )
    }

    fun queryTopTracks(artistId: String, market: String) {
        artistClient.getArtistTopTracks(artistId, market)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onSuccess = { artistsView.displayArtistTracks(ArrayList(it)) },
                        onError = { it.message?.let { artistsView.showError(it) } }
                )
    }

    fun querySimilarArtists(artistId: String) {
        artistClient.getArtistRelatedArtists(artistId)
                .flatMapObservable { it.toObservable() }
                .toSortedList { lhs, rhs ->
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
                        onSuccess = { artistsView.displaySimilarArtists(it) },
                        onError = { it.message?.let { artistsView.showError(it) } }
                )
    }

    fun getAlbumsObservable(artistId: String): Single<List<Track>> {
        var album: Album? = null
        return artistClient.getArtistAlbums(artistId)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { artistsView.displayArtistAlbums(it) }
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
    }

}