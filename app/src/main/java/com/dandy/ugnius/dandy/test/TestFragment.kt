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
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_test.*
import javax.inject.Inject


class TestFragment : Fragment() {

    @Inject lateinit var apiClient: APIClient

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (activity?.applicationContext as App).mainComponent?.inject(this)
        return inflater.inflate(R.layout.fragment_test, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        apiClient.getArtistAlbums("2rhFzFmezpnW82MNqEKVry", "album,single")
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val albums = it.filter { it.albumType == "album" }
                val singles = it.filter { it.albumType == "single" }
                albumsRecycler.adapter = HorizontalAlbumsAdapter(context!!, albums)
                albumsRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                singlesRecycler.adapter = HorizontalAlbumsAdapter(context!!, singles)
                singlesRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            }
    }
}
