package com.dandy.ugnius.dandy.artist.view.adapters

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dandy.ugnius.dandy.*
import com.dandy.ugnius.dandy.databinding.SimilarArtistEntryBinding
import com.dandy.ugnius.dandy.model.entities.Artist
import com.dandy.ugnius.dandy.model.entities.Track
import kotlinx.android.synthetic.main.similar_artist_entry.view.*

class SimilarArtistsAdapter(
    context: Context,
    private val onTrackClicked: (Track, List<Track>) -> Unit,
    private val onArtistClicked: (Artist) -> Unit
) : RecyclerView.Adapter<SimilarArtistsAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)
    var unmodifiableEntries: List<Artist>? = null
    private var entries = listOf<Artist>()

    fun setSimilarArtists(entries: List<Artist>) {
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

    inner class ViewHolder(private val binding: SimilarArtistEntryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(artist: Artist) {
            binding.artist = artist
            with(binding.root) {
                topThreeSongsRecycler.layoutManager = LinearLayoutManager(context)
//                topThreeSongsRecycler.adapter = TracksAdapter(context, onTrackClicked).apply { setAllTracks(artist.tracks!!) }
                artistInfo.setOnClickListener { onArtistClicked(artist) }
                favorite.setOnClickListener { favorite.playAnimation() }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<SimilarArtistEntryBinding>(inflater, R.layout.similar_artist_entry, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = entries.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(entries[position])
}