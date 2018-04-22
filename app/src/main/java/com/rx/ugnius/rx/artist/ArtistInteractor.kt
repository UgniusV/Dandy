package com.rx.ugnius.rx.artist

import com.rx.ugnius.rx.api.APIClient
import com.rx.ugnius.rx.api.RetrofitConfigurator
import io.reactivex.android.schedulers.AndroidSchedulers

class ArtistInteractor {

    private val apiClient = RetrofitConfigurator.configure().create(APIClient::class.java)

    fun getArtist(artistId: String) {
        apiClient.getArtist(artistId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(onSuccess = {
                    name.text = it.name
                    listeners.text = "Monthly listeners ${it.followers.total}"
                    it.images.firstOrNull()?.url?.let { Glide.with(this).load(it).into(background) }
                }, onError = {
                    //show error
                })
    }
}