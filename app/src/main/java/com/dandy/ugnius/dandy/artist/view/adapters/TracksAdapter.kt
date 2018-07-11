package com.dandy.ugnius.dandy.artist.view.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.dandy.ugnius.dandy.R
import com.dandy.ugnius.dandy.model.entities.Track
import com.dandy.ugnius.dandy.artist.common.secondOrNull
import kotlinx.android.synthetic.main.track_cell_entry.view.*

class TracksAdapter(
    context: Context,
    private val onTrackClicked: (Track) -> Unit
) : RecyclerView.Adapter<TracksAdapter.ViewHolder>() {

    var entries = listOf<Track>()
    set(value) {
        field = value
        notifyDataSetChanged()
    }
    private val inflater = LayoutInflater.from(context)
    private val requestManager = Glide.with(context)

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.track_cell_entry, parent, false))
    }

    override fun getItemCount() = entries.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.itemView) {
            val entry = entries[position]
            trackIndex.text = (position + 1).toString()
            requestManager.load(entry.images[1]).into(trackImage)
            trackTitle.text = entry.name
            trackArtist.text = entry.artists
            trackDuration.text = entry.duration
            setOnClickListener { onTrackClicked(entry) }
        }
    }

}