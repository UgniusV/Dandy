package com.dandy.ugnius.dandy.library.view

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.PagerAdapter
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.App
import com.dandy.ugnius.dandy.*
import com.dandy.ugnius.dandy.artist.view.adapters.AlbumsAdapter
import com.dandy.ugnius.dandy.artist.view.adapters.TracksAdapter
import com.dandy.ugnius.dandy.artist.view.adapters.TracksAdapterDelegate
import com.dandy.ugnius.dandy.artist.view.decorations.ItemOffsetDecoration
import com.dandy.ugnius.dandy.library.view.utilities.SwipeToDeleteListener
import com.dandy.ugnius.dandy.login.viewmodel.ViewModelFactory
import com.dandy.ugnius.dandy.main.MainActivity
import com.dandy.ugnius.dandy.model.entities.Album
import com.dandy.ugnius.dandy.model.entities.Track
import kotlinx.android.synthetic.main.fragment_library.*
import javax.inject.Inject

//todo pasidometi placiau apie mvvm live data
class LibraryFragment : Fragment(), TracksAdapterDelegate {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private var viewModel: LibraryViewModel? = null
    private val tracksAdapter by lazy { TracksAdapter(context!!, this) }
    private val albumsAdapter by lazy {
        AlbumsAdapter(
            context = context!!,
            onAlbumClicked = { album -> (this::onAlbumClicked)(album) }
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity?.applicationContext as App).mainComponent?.inject(this)
        viewModel = withViewModel(viewModelFactory) {
            observe(getTracks()) {
                tracksAdapter.setAllTracks(it!!)
            }
            observe(getAlbums()) { albumsAdapter.setAlbums(it!!) }
        }
        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    //todo iskelti gal i koki fragmenta sita
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        libraryPager.offscreenPageLimit = ARTIST_PAGER_ENTRIES_COUNT
        libraryPager.adapter = LibraryPagerAdapter(context!!)
        libraryPagerTabs.setupWithViewPager(libraryPager)
    }

    //delete track for now
    override fun onArtistTrackClicked(currentTrack: Track, tracks: List<Track>) {
        (context as? MainActivity)?.onArtistTrackClicked(currentTrack, tracks)
//        viewModel?.deleteTrack(currentTrack)
    }

    private fun onAlbumClicked(album: Album) = (context as? MainActivity)?.onAlbumClicked(album)

    private inner class LibraryPagerAdapter(context: Context) : PagerAdapter() {

        private val inflater = LayoutInflater.from(context)

        override fun getCount() = LIBRARY_PAGER_ENTRIES_COUNT

        override fun isViewFromObject(view: android.view.View, `object`: Any) = view == `object`

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val libraryRecycler = inflater.inflate(R.layout.pager_recycler, container, false) as RecyclerView
            with(libraryRecycler) {
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
                }
                container.addView(this)
                return libraryRecycler
            }
        }

        override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
            container.removeView(view as android.view.View)
        }

        override fun getPageTitle(position: Int) = when (position) {
            0 -> context?.getString(R.string.songs)
            1 -> context?.getString(R.string.albums)
            else -> throw IllegalArgumentException("Invalid item count was specified")
        }
    }

}
