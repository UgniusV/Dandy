package com.dandy.ugnius.dandy.artist.view.adapters

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dandy.ugnius.dandy.*
import com.dandy.ugnius.dandy.databinding.ArtistEntryBinding
import com.dandy.ugnius.dandy.global.entities.Artist
import kotlinx.android.synthetic.main.artist_entry.view.*

interface ArtistsAdapterDelegate {
    fun onArtistClicked(artist: Artist)
}

class ArtistsAdapter(
    context: Context,
    private val tracksAdapterDelegate: TracksAdapterDelegate?,
    private val delegate: ArtistsAdapterDelegate?
) : RecyclerView.Adapter<ArtistsAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)
    private var unmodifiableEntries: List<Artist>? = null
    private var entries = listOf<Artist>()

    fun setArtists(entries: List<Artist>) {
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

    fun filter(query: CharSequence) {
        val filteredEntries = unmodifiableEntries?.filter { it.name.contains(query, true) }
        filteredEntries?.let { setArtists(it) }
    }

    inner class ViewHolder(private val binding: ArtistEntryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(artist: Artist) {
            binding.artist = artist
            binding.delegate = delegate
            with(binding.root) {
                topThreeSongsRecycler.layoutManager = LinearLayoutManager(context)
                topThreeSongsRecycler.adapter = TracksAdapter(context, tracksAdapterDelegate).apply { setTracks(artist.tracks!!) }
                favorite.setOnClickListener { favorite.playAnimation() }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ArtistEntryBinding>(inflater, R.layout.artist_entry, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = entries.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(entries[position])
}