package com.dandy.ugnius.dandy

import android.content.Context
import android.graphics.*
import android.graphics.drawable.PaintDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.Color.WHITE
import android.graphics.Color.BLACK
import android.graphics.Color.GREEN
import android.graphics.Shader.TileMode.CLAMP
import android.graphics.drawable.Drawable
import android.graphics.drawable.shapes.RectShape
import android.graphics.PorterDuff.Mode.MULTIPLY
import android.support.v7.graphics.Palette
import io.reactivex.Maybe
import android.support.v4.graphics.ColorUtils
import android.support.v4.graphics.drawable.DrawableCompat
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import com.github.florent37.viewanimator.ViewAnimator
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.util.*


fun <T> List<T?>.secondOrNull(): T? = if (size > 1) get(1) else null

fun <T> List<T>.second(): T = get(1)

fun <T> List<T>.third(): T = get(2)

fun <T> List<T>.fourth(): T = get(3)

fun Bitmap.extractSwatch(): Maybe<Palette.Swatch> {
    return Maybe.create<Palette.Swatch> { emitter ->
        if (isRecycled) {
            emitter.onComplete()
        } else {
            Palette.from(this).generate { palette ->
                val swatch = palette.vibrantSwatch ?: palette.mutedSwatch
                ?: palette.darkVibrantSwatch ?: palette.dominantSwatch
                if (swatch == null) {
                    emitter.onComplete()
                } else {
                    emitter.onSuccess(swatch)
                }
            }
        }
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

//todo 2 yra ihardcodintas kad 2 columns kai realiai ji reikia pasiduoti
fun getGridItemDimensions(context: Context): Int {
    val metrics = context.resources.displayMetrics
    val padding = 32
    val width = (metrics.widthPixels / metrics.density) - padding
    return dpToPx(context, (width / 2).toInt())
}

fun pxToDp(context: Context, px: Int): Int {
    val resources = context.resources
    val metrics = resources.displayMetrics
    return px / (metrics.densityDpi / 160f).toInt()
}

fun dpToPx(context: Context, dp: Int): Int {
    val metrics = context.resources.displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), metrics).toInt()
}

fun random(start: Int, end: Int) = Random().nextInt((end + 1) - start) + start

fun ImageView.activate(duration: Long = 250, color: Int? = GREEN) {
    ViewAnimator.animate(this)
        .duration(duration)
        .scale(1F, 1.2F)
        .onStart { color?.let { DrawableCompat.setTint(drawable, it) } }
        .start()
}

fun ImageView.deactivate(duration: Long = 250, color: Int? = BLACK) {
    ViewAnimator.animate(this)
        .duration(duration)
        .scale(1.2F, 1F)
        .onStart { color?.let { DrawableCompat.setTint(drawable, it) } }
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
