package com.rx.ugnius.rx.artist

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.rx.ugnius.rx.R
import com.rx.ugnius.rx.api.entities.Track
import com.rx.ugnius.rx.secondOrNull
import kotlinx.android.synthetic.main.track_cell_entry.view.*

class TracksAdapter(private val entries: List<Track>, context: Context) : RecyclerView.Adapter<TracksAdapter.ViewHolder>() {

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
            entry.album.images.secondOrNull()?.url?.let { requestManager.load(it).into(trackImage) }
            trackTitle.text = entry.name
            trackArtist.text = entry.artists.map { it.name }.reduce { acc, name -> "$acc $name" }
            trackDuration.text = entry.duration.toString()
        }
    }

}