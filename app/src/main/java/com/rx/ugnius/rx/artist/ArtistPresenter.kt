package com.rx.ugnius.rx.artist

import com.rx.ugnius.rx.artist.model.ArtistClient
import com.rx.ugnius.rx.artist.model.RetrofitConfigurator
import com.rx.ugnius.rx.artist.view.View
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy

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

    fun queryArtistTopTracks(artistId: String, market: String = "ES") {
        artistClient.getArtistTopTracks(artistId, market)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext = { artistsView.displayArtistTopTracks(it) },
                        onError = { it.message?.let { artistsView.showError(it) } }
                )
    }


    fun queryArtistAlbums(artistId: String) {
        artistClient.getArtistAlbums(artistId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext = {
                            artistsView.displayArtistAlbums(it)
                        },
                        onError = { it.message?.let { artistsView.showError(it) } }
                )
    }

    fun queryAllSongs() {

    }
}