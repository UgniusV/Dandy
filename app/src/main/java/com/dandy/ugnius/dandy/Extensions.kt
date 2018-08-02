package com.dandy.ugnius.dandy

import android.content.Context
import android.graphics.*
import android.graphics.drawable.PaintDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.Color.WHITE
import android.graphics.Shader.TileMode.CLAMP
import android.graphics.drawable.shapes.RectShape
import android.support.v7.graphics.Palette
import io.reactivex.Maybe
import android.support.v4.graphics.ColorUtils
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.github.florent37.viewanimator.ViewAnimator
import java.util.*


fun <T> List<T>.second(): T = get(1)

fun <T> List<T>.third(): T = get(2)

fun <T> List<T>.fourth(): T = get(3)

fun Bitmap.extractSwatch(): Maybe<Palette.Swatch> {
    return Maybe.create<Palette.Swatch> { emitter ->
        if (isRecycled) {
            emitter.onComplete()
        } else {
            Palette.from(this).generate { palette ->
                val swatch = palette?.vibrantSwatch ?: palette?.mutedSwatch
                ?: palette?.darkVibrantSwatch ?: palette?.dominantSwatch
                if (swatch == null) {
                    emitter.onComplete()
                } else {
                    emitter.onSuccess(swatch)
                }
            }
        }
    }
}

fun ImageView.loadBitmap(url: String, context: Context, onBitmapLoaded: (Bitmap) -> Unit) {
    post {
        Glide.with(context)
            .asBitmap()
            .load(url)
            .into(object : SimpleTarget<Bitmap>(width, height) {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    setImageBitmap(resource)
                    onBitmapLoaded(resource)
                }
            })
    }
}

fun <T> LinkedList<T>.removeWithIndex(item: T): Int {
    val index = indexOf(item)
    remove(item)
    return index
}

fun adjustColorLuminance(color: Int, luminance: Float): Int {
    val hsv = FloatArray(3)
    Color.colorToHSV(color, hsv)
    hsv[2] = luminance
    return Color.HSVToColor(hsv)
}

fun adjustColorLightness(color: Int, lightness: Float) = if (lightness in 0F..1F) {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(color, hsl)
    hsl[1] = 0F
    hsl[2] = lightness
    ColorUtils.HSLToColor(hsl)
} else {
    throw IllegalArgumentException("Lightness parameter must be a value between 0F and 1F")

}

fun getNumberOfColumns(context: Context, columnWidth: Int): Int {
    val metrics = context.resources.displayMetrics
    val width = metrics.widthPixels / metrics.density
    return width.toInt() / columnWidth
}

//2 is hardcoded we need to pass an actual value later on
fun getGridItemDimensions(context: Context): Int {
    val metrics = context.resources.displayMetrics
    val padding = 32
    val width = (metrics.widthPixels / metrics.density) - padding
    return dpToPx(context, (width / 2).toInt())
}

fun dpToPx(context: Context, dp: Int): Int {
    val metrics = context.resources.displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), metrics).toInt()
}

fun random(start: Int, end: Int) = Random().nextInt((end + 1) - start) + start

fun ImageView.upscale(duration: Long = 250) {
    ViewAnimator.animate(this)
        .duration(duration)
        .scale(1F, 1.2F)
        .start()
}

fun ImageView.downscale(duration: Long = 250) {
    ViewAnimator.animate(this)
        .duration(duration)
        .scale(1.2F, 1F)
        .start()
}

fun View.shade(color: Int, ratio: Float) {
    if (ratio < 0 && ratio > 1F) {
        throw IllegalArgumentException("Ratio must be a float value between 0 and 1")
    }
    val shader = object : ShapeDrawable.ShaderFactory() {
        override fun resize(width: Int, height: Int): Shader {
            return LinearGradient(
                (width / 2).toFloat(),
                0F,
                (width / 2).toFloat(),
                (height).toFloat(),
                intArrayOf(color, WHITE, WHITE),
                floatArrayOf(0f, ratio, 1f),
                CLAMP
            )
        }
    }
    with(PaintDrawable()) {
        shape = RectShape()
        shaderFactory = shader
        background = this
    }
}
