package com.dandy.ugnius.dandy.home.view.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dandy.ugnius.dandy.R
import com.dandy.ugnius.dandy.artist.view.adapters.ArtistsAdapterDelegate
import com.dandy.ugnius.dandy.artist.view.adapters.TracksAdapterDelegate
import com.dandy.ugnius.dandy.di.components.DaggerViewModelComponent
import com.dandy.ugnius.dandy.di.modules.GeneralModule
import com.dandy.ugnius.dandy.global.factories.ViewModelFactory
import com.dandy.ugnius.dandy.home.view.adapters.HomeAdapter
import com.dandy.ugnius.dandy.home.viewmodels.HomeViewModel
import com.dandy.ugnius.dandy.utilities.observe
import com.dandy.ugnius.dandy.utilities.withViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject


class HomeFragment : Fragment() {

    @Inject lateinit var viewModelFactory: ViewModelFactory
    private var viewModel: HomeViewModel? = null
    var tracksAdapterDelegate: TracksAdapterDelegate? = null
    var artistsAdapterDelegate: ArtistsAdapterDelegate? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        DaggerViewModelComponent.builder()
            .generalModule(GeneralModule(context!!))
            .build()
            .inject(this)

        viewModel = withViewModel(viewModelFactory) {
            observe(entry) {
                val homeAdapterEntry = HomeAdapter.HomeAdapterEntry(it!!.tracks, it.artists)
                homeRecycler.layoutManager = LinearLayoutManager(context)
                homeRecycler.adapter = HomeAdapter(context!!, homeAdapterEntry, tracksAdapterDelegate, artistsAdapterDelegate)
            }
            getEntries()
        }

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.requestApplyInsets(homeRecycler)
    }

}