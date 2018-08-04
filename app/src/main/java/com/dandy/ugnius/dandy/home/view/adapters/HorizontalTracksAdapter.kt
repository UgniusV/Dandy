package com.dandy.ugnius.dandy.home.view.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.dandy.ugnius.dandy.R
import com.dandy.ugnius.dandy.artist.view.adapters.TracksAdapterDelegate
import com.dandy.ugnius.dandy.global.entities.Track
import com.dandy.ugnius.dandy.utilities.second
import kotlinx.android.synthetic.main.collection_cell_entry.view.*

class HorizontalTracksAdapter(
    context: Context,
    private val entries: List<Track>,
    private val tracksAdapterDelegate: TracksAdapterDelegate?
) : RecyclerView.Adapter<HorizontalTracksAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)
    private val requestManager = Glide.with(context)

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.collection_cell_entry, parent, false))
    }

    override fun getItemCount() = entries.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = entries[position]
        with(holder.view) {
            title.text = entry.name
            description.text = entry.artists
            requestManager.load(entry.images.second()).into(artwork)
            setOnClickListener { tracksAdapterDelegate?.onTrackClicked(entry, entries) }
        }
    }
}