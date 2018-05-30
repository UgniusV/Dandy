package com.rx.ugnius.rx.artist.view.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.rx.ugnius.rx.R
import com.rx.ugnius.rx.artist.model.entities.Album
import com.rx.ugnius.rx.artist.common.secondOrNull
import kotlinx.android.synthetic.main.album_cell_entry.view.*

class AlbumsAdapter(context: Context) : RecyclerView.Adapter<AlbumsAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)
    private val glide = Glide.with(context)
    var entries = listOf<Album>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.album_cell_entry, parent, false))
    }

    override fun getItemCount() = entries.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = entries[position]
        with(holder.itemView) {
            entry.images.secondOrNull()?.url?.let { glide.load(it).into(artwork) }
            title.text = entry.name
            releaseDate.text = entry.releaseDate

        }
    }
}