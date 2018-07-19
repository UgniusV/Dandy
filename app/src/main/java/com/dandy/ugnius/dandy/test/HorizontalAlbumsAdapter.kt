package com.dandy.ugnius.dandy.test

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.dandy.ugnius.dandy.R
import com.dandy.ugnius.dandy.model.entities.Album
import com.dandy.ugnius.dandy.second
import com.dandy.ugnius.dandy.secondOrNull

class HorizontalAlbumsAdapter(context: Context, private val entries: List<Album>) : RecyclerView.Adapter<HorizontalAlbumsAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)
    private val glide = Glide.with(context)

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.album_cell_entry, parent, false))
    }

    override fun getItemCount() = entries.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = entries[position]
        with(holder.itemView) {
            val artwork = findViewById<ImageView>(R.id.artwork)
            val favorite = findViewById<ImageView>(R.id.favorite)
            val title = findViewById<TextView>(R.id.title)
            val releaseDate = findViewById<TextView>(R.id.releaseDate)
            glide.load(entry.images.second()).into(artwork)
            title.text = entry.name
            releaseDate.text = entry.releaseDate
        }
    }
}