package com.dandy.ugnius.dandy.artist.view.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.dandy.ugnius.dandy.R
import com.dandy.ugnius.dandy.model.entities.Track
import com.dandy.ugnius.dandy.second
import kotlinx.android.synthetic.main.track_entry.view.*

class TracksAdapter(
    context: Context,
    private val onTrackClicked: (Track, List<Track>) -> Unit
) : RecyclerView.Adapter<TracksAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)
    private val requestManager = Glide.with(context)
    var unmodifiableEntries: List<Track>? = null
    private var entries = listOf<Track>()

    fun setTopTracks(entries: List<Track>) {
        this.entries = entries
        notifyItemRangeInserted(0, entries.size)
    }

    fun setAllTracks(entries: List<Track>) {
        this.entries = entries
        if (unmodifiableEntries == null) {
            unmodifiableEntries = entries
        }
        notifyDataSetChanged()
    }

    fun reset() {
        entries = unmodifiableEntries ?: emptyList()
        notifyDataSetChanged()
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.track_entry, parent, false))
    }

    override fun getItemCount() = entries.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.itemView) {
            val entry = entries[position]
            trackIndex.text = (position + 1).toString()
            requestManager.load(entry.images.second()).into(trackImage)
            trackTitle.text = entry.name
            trackArtist.text = entry.artists
            trackDuration.text = entry.duration
            setOnClickListener { onTrackClicked(entry, entries) }
        }
    }

}