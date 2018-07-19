package com.dandy.ugnius.dandy.artist.view.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.ColorUtils
import android.support.v7.graphics.Palette
import android.support.v7.widget.RecyclerView
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.dandy.ugnius.dandy.R
import com.dandy.ugnius.dandy.extractSwatch
import com.dandy.ugnius.dandy.model.entities.Artist
import com.dandy.ugnius.dandy.shade
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.similar_artist_cell_entry.view.*
import kotlinx.android.synthetic.main.view_player.*
import java.text.NumberFormat
import java.util.*

class SimilarArtistsAdapter(private val context: Context) : RecyclerView.Adapter<SimilarArtistsAdapter.ViewHolder>() {

    var entries = listOf<Artist>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    private val inflater = LayoutInflater.from(context)
    private val requestManager = Glide.with(context)
    private var expandedPosition = -1
    private val formatter = NumberFormat.getNumberInstance(Locale.US)

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.similar_artist_cell_entry, parent, false))
    }

    override fun getItemCount() = entries.size

    /*
    Glide.with(context ?: return@post)
                .asBitmap()
                .load(track.images.first())
                .into(object : SimpleTarget<Bitmap>(artwork!!.width, artwork!!.height) {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        artwork?.setImageBitmap(resource)
                        resource.extractSwatch()
                            .subscribeBy(
                                onSuccess = { setAccentColor(it) },
                                onComplete = { playbackControls?.background = ColorDrawable(Color.WHITE) }
                            )
     */

    private fun setAccentColor(view: View, swatch: Palette.Swatch) {
        val transparentWhite = ContextCompat.getColor(context, R.color.opaqueWhite)
        val blendedColor = ColorUtils.blendARGB(transparentWhite, swatch.rgb, 0.1F)
        view.shade(color = blendedColor, ratio = 0.5F)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.itemView) {
            val entry = entries[position]
            requestManager.load(entry.images[2]).into(artwork)
            followers.text = String.format(context.getString(R.string.followers, formatter.format(entry.followers)))
            genres.text = if (entry.genres.isEmpty()) {
                context.getString(R.string.various_genres)
            } else {
                String.format(context.getString(R.string.genres, entry.genres))
            }
            artistName.text = entry.name


            val isExpanded = position == expandedPosition
            details.visibility = if (isExpanded) View.VISIBLE else View.GONE
            holder.itemView.isActivated = isExpanded
            expand.setOnClickListener {
                expandedPosition = if (isExpanded) -1 else position
                TransitionManager.beginDelayedTransition(content)
                notifyItemChanged(position);
            }
        }
    }
}