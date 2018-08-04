package com.dandy.ugnius.dandy.home.view.adapters

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dandy.ugnius.dandy.utilities.HOME_ADAPTER_ENTRIES_COUNT
import com.dandy.ugnius.dandy.R
import com.dandy.ugnius.dandy.artist.view.adapters.ArtistsAdapterDelegate
import com.dandy.ugnius.dandy.artist.view.adapters.TracksAdapterDelegate
import com.dandy.ugnius.dandy.global.entities.Artist
import com.dandy.ugnius.dandy.global.entities.Track
import kotlinx.android.synthetic.main.collection_entry.view.*

class HomeAdapter(
    context: Context,
    private val entry: HomeAdapterEntry,
    private val tracksDelegate: TracksAdapterDelegate?,
    private val artistsAdapterDelegate: ArtistsAdapterDelegate?
) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
    class HomeAdapterEntry(val tracks: List<Track>, val artists: List<Artist>)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.collection_entry, parent, false))
    }

    override fun getItemCount() = HOME_ADAPTER_ENTRIES_COUNT

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.view) {
            entriesRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            if (position == 0) {
                header.text = context?.getString(R.string.songs_for_you)
                entriesRecycler.adapter = HorizontalTracksAdapter(context, entry.tracks, tracksDelegate)
            } else {
                header.text = context?.getString(R.string.artists_for_you)
                entriesRecycler.adapter = HorizontalArtistsAdapter(context, entry.artists, artistsAdapterDelegate)
            }
        }
    }
}