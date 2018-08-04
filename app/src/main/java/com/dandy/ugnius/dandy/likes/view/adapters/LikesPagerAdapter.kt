package com.dandy.ugnius.dandy.likes.view.adapters

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dandy.ugnius.dandy.utilities.LIKES_PAGER_ENTRIES_COUNT
import com.dandy.ugnius.dandy.R
import com.dandy.ugnius.dandy.artist.view.adapters.*
import com.dandy.ugnius.dandy.artist.view.decorations.ItemOffsetDecoration

class LikesPagerAdapter(
    private val context: Context,
    tracksAdapterDelegate: TracksAdapterDelegate?,
    albumsAdapterDelegate: AlbumsAdapterDelegate?
): PagerAdapter() {

    private val inflater = LayoutInflater.from(context)
    val tracksAdapter = TracksAdapter(context, tracksAdapterDelegate)
    val albumsAdapter = AlbumsAdapter(context, albumsAdapterDelegate)

    override fun getCount() = LIKES_PAGER_ENTRIES_COUNT

    override fun isViewFromObject(view: android.view.View, `object`: Any) = view == `object`

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val likesRecycler = inflater.inflate(R.layout.recycler, container, false) as RecyclerView
        with(likesRecycler) {
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
            return likesRecycler
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
        container.removeView(view as android.view.View)
    }

    override fun getPageTitle(position: Int): String = when (position) {
        0 -> context.getString(R.string.songs)
        1 -> context.getString(R.string.albums)
        else -> throw IllegalArgumentException("Invalid item count was specified")
    }

}