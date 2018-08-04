package com.dandy.ugnius.dandy.artist.view

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
import com.dandy.ugnius.dandy.global.entities.Album
import com.dandy.ugnius.dandy.global.entities.Artist
import com.dandy.ugnius.dandy.global.entities.Track
import com.dandy.ugnius.dandy.artist.presenter.ArtistViewModel
import android.view.*
import com.dandy.ugnius.dandy.*
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.view_artist.*
import com.dandy.ugnius.dandy.main.MainActivity
import android.widget.ImageView
import com.dandy.ugnius.dandy.artist.view.adapters.*
import com.dandy.ugnius.dandy.databinding.ViewArtistBinding
import com.dandy.ugnius.dandy.di.components.DaggerViewModelComponent
import com.dandy.ugnius.dandy.di.modules.GeneralModule
import com.dandy.ugnius.dandy.global.factories.ViewModelFactory
import com.jakewharton.rxbinding2.support.v4.view.RxViewPager
import com.jakewharton.rxbinding2.support.v7.widget.RxSearchView
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class ArtistFragment : Fragment(), TracksAdapterDelegate, AlbumsAdapterDelegate, ArtistsAdapterDelegate {

    private val pagerAdapter by lazy { ArtistPagerAdapter(context!!, this, this, this) }
    @Inject lateinit var viewModelFactory: ViewModelFactory
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
        setHasOptionsMenu(true)
        (activity as MainActivity).setSupportActionBar(toolbar)
        artistPager.offscreenPageLimit = ARTIST_PAGER_ENTRIES_COUNT
        artistPager.adapter = pagerAdapter
        artistPagerTabs.setupWithViewPager(artistPager)
        collapsingAppBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, offset ->
            if (offset == -appBarLayout.totalScrollRange) {
                searchItem?.isVisible = true
                searchView?.visibility = VISIBLE
                (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
            } else if (offset == 0) {
                searchItem?.isVisible = false
                searchView?.visibility = GONE
                toolbar.collapseActionView()
                (activity as? MainActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }
        })
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

    private fun setArtistInfo(artist: Artist) {
        binding?.artist = artist
        background?.loadBitmap(artist.images.first(), context!!) {
            it.extractSwatch().subscribeBy(
                onSuccess = { setAccentColor(it) },
                onComplete = { artistPager.background = ColorDrawable(WHITE) }
            )
        }
    }

    override fun onStop() {
        super.onStop()
    }

    //todo move this to activity edelegate
    override fun onTrackClicked(currentTrack: Track, allTracks: List<Track>) {
        (context as? MainActivity)?.onArtistTrackClicked(currentTrack, allTracks)
    }

    override fun onAlbumClicked(album: Album) {
        (context as? MainActivity)?.onAlbumClicked(album)
    }

    private fun setAccentColor(swatch: Palette.Swatch) {
        artistPager.shade(color = Utilities.whiteBlend(context!!, swatch.rgb, 0.3F), ratio = 0.2F)
        val adjustedColor = Drawables.lightenOrDarken(swatch.rgb, 0.4)
        artistPagerTabs?.let {
            it.setSelectedTabIndicatorColor(adjustedColor)
            it.setTabTextColors(adjustedColor, adjustedColor)
        }
        collapsingLayout?.let {
            it.setContentScrimColor(swatch.rgb)
            it.setStatusBarScrimColor(swatch.rgb)
        }
    }

    override fun onArtistClicked(artist: Artist) {
        (context as MainActivity).onArtistClicked(artist.id)
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
        return when(item?.itemId) {
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

}
