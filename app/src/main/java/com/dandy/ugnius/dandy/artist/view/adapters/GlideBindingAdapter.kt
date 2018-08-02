package com.dandy.ugnius.dandy.artist.view.adapters

import android.databinding.BindingAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide

object GlideBindingAdapter {

    @JvmStatic
    @BindingAdapter("imageUrl")
    fun setImageResource(image: ImageView, imageUrl: String) {
        Glide.with(image.context)
            .load(imageUrl)
            .into(image)
    }

}