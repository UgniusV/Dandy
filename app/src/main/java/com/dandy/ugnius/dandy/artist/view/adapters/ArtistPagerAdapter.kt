package com.dandy.ugnius.dandy.artist.view.adapters

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dandy.ugnius.dandy.ARTIST_PAGER_ENTRIES_COUNT
import com.dandy.ugnius.dandy.R
import com.dandy.ugnius.dandy.artist.view.decorations.ItemOffsetDecoration

class ArtistPagerAdapter(
    private val context: Context,
    tracksAdapterDelegate: TracksAdapterDelegate,
    albumsAdapterDelegate: AlbumsAdapterDelegate,
    artistsAdapterDelegate: ArtistsAdapterDelegate
) : PagerAdapter() {

    private val inflater = LayoutInflater.from(context)
    val tracksAdapter = TracksAdapter(context, tracksAdapterDelegate)
    val albumsAdapter = AlbumsAdapter(context, albumsAdapterDelegate)
    val artistsAdapter = ArtistsAdapter(context, tracksAdapterDelegate, artistsAdapterDelegate)

    fun reset(position: Int) {
        when (position) {
            0 -> tracksAdapter.reset()
            1 -> albumsAdapter.reset()
            else -> artistsAdapter.reset()
        }
    }

    fun filter(position: Int, query: CharSequence) {
        when (position) {
            0 -> tracksAdapter.filter(query)
            1 -> albumsAdapter.filter(query)
            else -> artistsAdapter.filter(query)
        }
    }

    override fun getCount() = ARTIST_PAGER_ENTRIES_COUNT

    override fun isViewFromObject(view: android.view.View, `object`: Any) = view == `object`

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val artistRecycler = inflater.inflate(R.layout.recycler, container, false) as RecyclerView
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
                    adapter = artistsAdapter
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

    override fun getPageTitle(position: Int): String = when (position) {
        0 -> context.getString(R.string.songs)
        1 -> context.getString(R.string.albums)
        2 -> context.getString(R.string.similar)
        else -> throw IllegalArgumentException("Invalid item count was specified")
    }

}