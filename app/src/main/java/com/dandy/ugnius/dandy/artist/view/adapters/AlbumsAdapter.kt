package com.dandy.ugnius.dandy.artist.view.adapters

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dandy.ugnius.dandy.R
import com.dandy.ugnius.dandy.databinding.AlbumEntryBinding
import com.dandy.ugnius.dandy.utilities.getGridItemDimensions
import com.dandy.ugnius.dandy.global.entities.Album
import com.makeramen.roundedimageview.RoundedImageView

interface AlbumsAdapterDelegate {
    fun onAlbumClicked(album: Album)
}
class AlbumsAdapter(
    context: Context,
    private val delegate: AlbumsAdapterDelegate?
) : RecyclerView.Adapter<AlbumsAdapter.ViewHolder>() {

    private val dimension = getGridItemDimensions(context)
    private val inflater = LayoutInflater.from(context)
    private var unmodifiableEntries: List<Album>? = null
    private var entries = listOf<Album>()

    fun setAlbums(entries: List<Album>) {
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
        filteredEntries?.let { setAlbums(it) }
    }

    inner class ViewHolder(private val binding: AlbumEntryBinding) : RecyclerView.ViewHolder(binding.root) {
        val view = binding.root
        init {
            view.layoutParams.width = dimension
            view.findViewById<RoundedImageView>(R.id.artwork).layoutParams.height = dimension
        }
        fun bind(album: Album) {
            binding.album = album
            binding.delegate = delegate
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<AlbumEntryBinding>(inflater, R.layout.album_entry, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = entries.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(entries[position])
}