package com.dandy.ugnius.dandy.test

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.App
import com.dandy.ugnius.dandy.R
import com.dandy.ugnius.dandy.model.clients.APIClient
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_test.*
import javax.inject.Inject


class TestFragment : Fragment() {

    @Inject
    lateinit var apiClient: APIClient

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (activity?.applicationContext as App).mainComponent?.inject(this)
        return inflater.inflate(R.layout.fragment_test, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var albumId = "4aawyAB9vmqN3uQ7FjRGT"
        val observable = Observable.defer { apiClient.getAlbumsTracks(albumId) }
        observable
            .retryWhen {
                println("flow: retry when block")
                albumId = "4aawyAB9vmqN3uQ7FjRGTy"
                Observable.just("@")
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    it.forEach {
                        println("flow: received track ${it.name}")
                    }
                },
                onComplete = {
                    println("flow: completed")
                },
                onError = {
                    println("flow: error ${it.message}")
                }
            )
    }
}
