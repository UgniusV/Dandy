package com.dandy.ugnius.dandy.artist.view.adapters

import android.content.Context
import java.util.Locale.US
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.dandy.ugnius.dandy.*
import com.dandy.ugnius.dandy.model.entities.Artist
import com.dandy.ugnius.dandy.model.entities.Track
import kotlinx.android.synthetic.main.similar_artist_cell_entry.view.*
import java.text.NumberFormat

class SimilarArtistsAdapter(
    private val context: Context,
    private val onTrackClicked: (Track, List<Track>) -> Unit,
    private val onArtistClicked: (Artist) -> Unit
) : RecyclerView.Adapter<SimilarArtistsAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)
    private val requestManager = Glide.with(context)
    private val formatter = NumberFormat.getNumberInstance(US)
    var entries = listOf<Artist>()
        set(value) {
            field = value
            notifyItemRangeInserted(0, entries.size)
        }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.similar_artist_cell_entry, parent, false))
    }

    override fun getItemCount() = entries.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.itemView) {
            val entry = entries[position]
            requestManager.load(entry.images[2]).into(artwork)
            artistName.text = entry.name
            followers.text = String.format(context.getString(R.string.followers, formatter.format(entry.followers)))
            genres.text = if (entry.genres.isEmpty()) {
                context.getString(R.string.various_genres)
            } else {
                String.format(context.getString(R.string.genres, entry.genres))
            }
            topThreeSongsRecycler.layoutManager = LinearLayoutManager(context)
            topThreeSongsRecycler.adapter = TracksAdapter(context, onTrackClicked).apply { entries = entry.tracks!! }
            artistInfo.setOnClickListener { onArtistClicked(entry) }
            favorite.setOnClickListener { favorite.playAnimation() }
        }
    }
}