package com.dandy.ugnius.dandy.artist.view

import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Color.WHITE
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View.VISIBLE
import android.view.View.GONE
import android.support.v4.app.Fragment
import android.support.v4.view.PagerAdapter
import android.support.v7.graphics.Palette
import android.support.v7.widget.*
import com.dandy.ugnius.dandy.model.entities.Album
import com.dandy.ugnius.dandy.model.entities.Artist
import com.dandy.ugnius.dandy.model.entities.Track
import com.dandy.ugnius.dandy.artist.presenter.ArtistViewModel
import android.view.*
import com.App
import com.dandy.ugnius.dandy.*
import com.dandy.ugnius.dandy.model.clients.APIClient
import com.dandy.ugnius.dandy.artist.view.adapters.AlbumsAdapter
import com.dandy.ugnius.dandy.artist.view.adapters.SimilarArtistsAdapter
import com.dandy.ugnius.dandy.artist.view.adapters.TracksAdapter
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.view_artist.*
import com.dandy.ugnius.dandy.main.MainActivity
import javax.inject.Inject
import com.dandy.ugnius.dandy.artist.view.decorations.ItemOffsetDecoration
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import com.dandy.ugnius.dandy.artist.view.adapters.TracksAdapterDelegate
import com.dandy.ugnius.dandy.artist.view.interfaces.ArtistView
import com.dandy.ugnius.dandy.databinding.ViewArtistBinding
import com.dandy.ugnius.dandy.login.viewmodel.ViewModelFactory
import com.jakewharton.rxbinding2.support.v4.view.RxViewPager
import com.jakewharton.rxbinding2.support.v7.widget.RxSearchView
import io.reactivex.disposables.CompositeDisposable

class ArtistFragment : Fragment(), TracksAdapterDelegate {

    //todo live data su search queriu

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private var viewModel: ArtistViewModel? = null
    private var searchItem: MenuItem? = null
    private var searchView: SearchView? = null
    private var compositeDisposable = CompositeDisposable()
    private var binding: ViewArtistBinding? = null

    private val tracksAdapter by lazy { TracksAdapter(context!!, this) }
    private val albumsAdapter by lazy {
        AlbumsAdapter(
            context = context!!,
            onAlbumClicked = { album -> (this::onAlbumClicked)(album) }
        )
    }
    private val similarArtistsAdapter by lazy {
        SimilarArtistsAdapter(
            context = context!!,
            onTrackClicked = { currentTrack, tracks -> (this::onArtistTrackClicked)(currentTrack, tracks) },
            onArtistClicked = { artist -> (context as MainActivity).onArtistClicked(artist.id) }
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity?.applicationContext as App).mainComponent?.inject(this)
        viewModel = withViewModel(viewModelFactory) {
            observe(artist) { setArtistInfo(it!!) }
            observe(topTracks) { tracksAdapter.setTracks(it!!) }
            observe(tracks) { tracksAdapter.setAllTracks(it!!) }
            observe(albums) { albumsAdapter.setAlbums(it!!) }
            observe(similarArtists) { similarArtistsAdapter.setSimilarArtists(it!!) }
        }
//        viewModel.artist.po
        binding = DataBindingUtil.inflate(inflater, R.layout.view_artist, container, false)
        return binding?.root
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        arguments?.getString("artistId")?.let { viewModel?.query(it, "ES", "album,single") }
        setHasOptionsMenu(true)
        (activity as MainActivity).setSupportActionBar(toolbar)
        artistPager.offscreenPageLimit = ARTIST_PAGER_ENTRIES_COUNT
        artistPager.adapter = ArtistPagerAdapter(context!!)
        artistPagerTabs.setupWithViewPager(artistPager)
        collapsingAppBar.addOnOffsetChangedListener { appBarLayout, offset ->
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
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val pagerDisposable = RxViewPager.pageSelections(artistPager).subscribe {
            searchView!!.setQuery("", false)
            when (it) {
                0 -> tracksAdapter.reset()
                1 -> albumsAdapter.reset()
                else -> similarArtistsAdapter.reset()
            }
        }
        val searchViewDisposable = RxSearchView.queryTextChanges(searchView!!).subscribe { query ->
            when (artistPager.currentItem) {
                0 -> {
                    val filteredEntries = tracksAdapter.unmodifiableEntries?.filter { it.name.contains(query, true) }
                    filteredEntries?.let { tracksAdapter.setAllTracks(filteredEntries) }
                }
                1 -> {
                    val filteredEntries = albumsAdapter.unmodifiableEntries?.filter { it.name.contains(query, true) }
                    filteredEntries?.let { albumsAdapter.setAlbums(it) }
                }
                else -> {
                    val filteredEntries = similarArtistsAdapter.unmodifiableEntries?.filter { it.name.contains(query, true) }
                    filteredEntries?.let { similarArtistsAdapter.setSimilarArtists(it) }
                }
            }
        }
        compositeDisposable.addAll(pagerDisposable, searchViewDisposable)

    }

    override fun onArtistTrackClicked(currentTrack: Track, tracks: List<Track>) {
        (context as? MainActivity)?.onArtistTrackClicked(currentTrack, tracks)
    }

    private fun onAlbumClicked(album: Album) = (context as? MainActivity)?.onAlbumClicked(album)

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater?) {
        (activity as MainActivity).menuInflater.inflate(R.menu.artist_toolbar_menu, menu)
        searchItem = menu.findItem(R.id.action_search)
        searchView = (searchItem?.actionView as? SearchView)?.apply {
            setIconifiedByDefault(false)
            queryHint = context?.getString(R.string.search)
            (findViewById<ImageView>(android.support.v7.appcompat.R.id.search_mag_icon)).setImageDrawable(null)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == R.id.actionFavorite) {
            //add artist to favorites
            true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    private inner class ArtistPagerAdapter(context: Context) : PagerAdapter() {

        private val inflater = LayoutInflater.from(context)

        override fun getCount() = ARTIST_PAGER_ENTRIES_COUNT

        override fun isViewFromObject(view: android.view.View, `object`: Any) = view == `object`

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val artistRecycler = inflater.inflate(R.layout.pager_recycler, container, false) as RecyclerView
            with(artistRecycler) {
                when (position) {
                    0 -> {
                        adapter = tracksAdapter
                        layoutManager = LinearLayoutManager(context)
                    }
                    1 -> {
                        adapter = albumsAdapter
                        layoutManager = GridLayoutManager(context, 2)
                        addItemDecoration(ItemOffsetDecoration(context, 4))
                    }
                    2 -> {
                        adapter = similarArtistsAdapter
                        layoutManager = LinearLayoutManager(context)
                    }
                }
                container.addView(this)
                return artistRecycler
            }
        }

        override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
            container.removeView(view as android.view.View)
        }

        override fun getPageTitle(position: Int) = when (position) {
            0 -> context?.getString(R.string.songs)
            1 -> context?.getString(R.string.albums)
            2 -> context?.getString(R.string.similar)
            else -> throw IllegalArgumentException("Invalid item count was specified")
        }
    }
}
