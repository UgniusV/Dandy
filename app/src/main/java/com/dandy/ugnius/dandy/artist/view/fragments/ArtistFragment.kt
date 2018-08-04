package com.dandy.ugnius.dandy.artist.view.fragments

import android.databinding.DataBindingUtil
import android.graphics.Color.WHITE
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.view.View.VISIBLE
import android.view.View.GONE
import android.support.v4.app.Fragment
import android.support.v7.graphics.Palette
import android.support.v7.widget.*
import com.dandy.ugnius.dandy.global.entities.Artist
import com.dandy.ugnius.dandy.artist.viewmodels.ArtistViewModel
import android.view.*
import com.dandy.ugnius.dandy.*
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.view_artist.*
import com.dandy.ugnius.dandy.main.view.MainActivity
import android.widget.ImageView
import com.dandy.ugnius.dandy.artist.view.adapters.*
import com.dandy.ugnius.dandy.databinding.ViewArtistBinding
import com.dandy.ugnius.dandy.di.components.DaggerViewModelComponent
import com.dandy.ugnius.dandy.di.modules.GeneralModule
import com.dandy.ugnius.dandy.global.factories.ViewModelFactory
import com.dandy.ugnius.dandy.utilities.*
import com.jakewharton.rxbinding2.support.v4.view.RxViewPager
import com.jakewharton.rxbinding2.support.v7.widget.RxSearchView
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class ArtistFragment : Fragment() {

    @Inject lateinit var viewModelFactory: ViewModelFactory
    var tracksAdapterDelegate: TracksAdapterDelegate? = null
    var albumsAdapterDelegate: AlbumsAdapterDelegate? = null
    var artistsAdapterDelegate: ArtistsAdapterDelegate? = null
    private val pagerAdapter by lazy { ArtistPagerAdapter(context!!, tracksAdapterDelegate, albumsAdapterDelegate, artistsAdapterDelegate) }
    private var compositeDisposable = CompositeDisposable()
    private var viewModel: ArtistViewModel? = null
    private var binding: ViewArtistBinding? = null
    private var searchView: SearchView? = null
    private var searchItem: MenuItem? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        DaggerViewModelComponent.builder()
            .generalModule(GeneralModule(context!!))
            .build()
            .inject(this)
        viewModel = withViewModel(viewModelFactory) {
            observe(artist) { setArtistInfo(it!!) }
            observe(topTracks) { pagerAdapter.tracksAdapter.setTracks(it!!) }
            observe(tracks) { pagerAdapter.tracksAdapter.setTracks(it!!) }
            observe(albums) { pagerAdapter.albumsAdapter.setAlbums(it!!) }
            observe(similarArtists) { pagerAdapter.artistsAdapter.setArtists(it!!) }
        }
        binding = DataBindingUtil.inflate(inflater, R.layout.view_artist, container, false)
        return binding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        arguments?.getString("artistId")?.let { viewModel?.query(it, "ES", "album,single") }
        setupToolbar()
        setupViewPager()
        setupCollapsingLayout()
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)

        val pagerDisposable = RxViewPager.pageSelections(artistPager)
            .map { pagerAdapter.reset(it) }
            .subscribe { searchView?.setQuery("", false) }

        val searchViewDisposable = RxSearchView.queryTextChanges(searchView!!)
            .map { pagerAdapter.filter(artistPager.currentItem, it) }
            .subscribe()

        compositeDisposable.addAll(pagerDisposable, searchViewDisposable)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater?) {
        (activity as MainActivity).menuInflater.inflate(R.menu.artist_toolbar_menu, menu)
        searchItem = menu.findItem(R.id.search)
        searchView = (searchItem?.actionView as? SearchView)?.apply {
            setIconifiedByDefault(false)
            queryHint = context?.getString(R.string.search)
            (findViewById<ImageView>(android.support.v7.appcompat.R.id.search_mag_icon)).setImageDrawable(null)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.favorite -> true
            R.id.search -> super.onOptionsItemSelected(item)
            else -> {
                activity?.supportFragmentManager?.popBackStack()
                true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }

    private fun setupViewPager() {
        artistPager.offscreenPageLimit = ARTIST_PAGER_ENTRIES_COUNT
        artistPager.adapter = pagerAdapter
        artistPagerTabs.setupWithViewPager(artistPager)
    }

    private fun setupToolbar() {
        setHasOptionsMenu(true)
        (activity as MainActivity).setSupportActionBar(toolbar)
    }

    private fun setupCollapsingLayout() {

        fun toggleSearchView(toggle: Boolean) {
            searchItem?.isVisible = toggle
            searchView?.visibility = if (toggle) VISIBLE else GONE
            (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(!toggle)
        }

        collapsingAppBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, offset ->
            val isCollapsed = offset == -appBarLayout.totalScrollRange
            if (isCollapsed) {
                toggleSearchView(true)
            } else if (offset == 0) {
                toggleSearchView(false)
            }
        })
    }

    private fun setArtistInfo(artist: Artist) {
        binding?.artist = artist
        background?.loadBitmap(artist.images.first(), context!!) {
            it.extractSwatch().subscribeBy(
                onSuccess = { setAccentColor(it) },
                onComplete = { artistPager.background = ColorDrawable(WHITE) }
            )
        }
    }

    private fun setAccentColor(swatch: Palette.Swatch) {
        artistPager?.shade(color = Utilities.whiteBlend(context!!, swatch.rgb, 0.3F), ratio = 0.2F)
        val adjustedColor = adjustColorLightness(color = swatch.rgb, lightness = 0.3F)
        artistPagerTabs?.let {
            it.setSelectedTabIndicatorColor(adjustedColor)
            it.setTabTextColors(adjustedColor, adjustedColor)
        }
        collapsingLayout?.let {
            it.setContentScrimColor(swatch.rgb)
            it.setStatusBarScrimColor(swatch.rgb)
        }
    }

}
