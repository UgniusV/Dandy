package com.dandy.ugnius.dandy.artist.view.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.dandy.ugnius.dandy.R
import com.dandy.ugnius.dandy.getGridItemDimensions
import com.dandy.ugnius.dandy.model.entities.Album
import com.dandy.ugnius.dandy.second
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.album_cell_entry.view.*

class AlbumsAdapter(
    context: Context,
    private val onAlbumClicked: (Album) -> Unit
) : RecyclerView.Adapter<AlbumsAdapter.ViewHolder>() {

    private val dimension = getGridItemDimensions(context)
    private val inflater = LayoutInflater.from(context)
    private val glide = Glide.with(context)
    var entries = listOf<Album>()
        set(value) {
            field = value
            notifyItemRangeInserted(0, entries.size)
        }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        init {
            view.layoutParams.width = dimension
            view.findViewById<RoundedImageView>(R.id.artwork).layoutParams.height = dimension
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.album_cell_entry, parent, false))
    }

    override fun getItemCount() = entries.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = entries[position]
        with(holder.itemView) {
            glide.load(entry.images.second()).into(artwork)
            title.text = entry.name
            releaseDate.text = entry.releaseDate
            setOnClickListener { onAlbumClicked(entry) }
        }
    }
}