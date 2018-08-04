package com.dandy.ugnius.dandy.artist.view.adapters

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dandy.ugnius.dandy.R
import com.dandy.ugnius.dandy.databinding.TrackEntryBinding
import com.dandy.ugnius.dandy.global.entities.Track

interface TracksAdapterDelegate {
    fun onTrackClicked(currentTrack: Track, allTracks: List<Track>)
}

class TracksAdapter(
    context: Context,
    private val delegate: TracksAdapterDelegate
) : RecyclerView.Adapter<TracksAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)
    private var unmodifiableEntries: List<Track>? = null
    private var entries = listOf<Track>()

    fun setTracks(entries: List<Track>) {
        this.entries = entries
        if (unmodifiableEntries == null) {
            unmodifiableEntries = entries
        }
        notifyDataSetChanged()
    }

    fun filter(query: CharSequence) {
        val filteredEntries = unmodifiableEntries?.filter { it.name.contains(query, true) }
        filteredEntries?.let { setTracks(it) }
    }

    fun reset() {
        entries = unmodifiableEntries ?: emptyList()
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: TrackEntryBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val entry = entries[position]
            binding.track = entry
            binding.index = position
            binding.entries = entries
            binding.delegate = delegate
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<TrackEntryBinding>(inflater, R.layout.track_entry, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = entries.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(position)

}