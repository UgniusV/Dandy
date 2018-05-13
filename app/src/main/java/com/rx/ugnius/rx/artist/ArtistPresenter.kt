package com.rx.ugnius.rx.artist

import com.rx.ugnius.rx.artist.model.ArtistClient
import com.rx.ugnius.rx.artist.model.RetrofitConfigurator
import com.rx.ugnius.rx.artist.model.entities.Album
import com.rx.ugnius.rx.artist.model.entities.Track
import com.rx.ugnius.rx.artist.view.View
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
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

    fun getAlbumsOberservable(artistId: String): Single<List<Track>> {
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