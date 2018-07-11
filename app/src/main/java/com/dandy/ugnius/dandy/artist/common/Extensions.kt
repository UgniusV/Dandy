package com.dandy.ugnius.dandy.artist.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.graphics.Palette
import io.reactivex.Maybe
import android.support.v4.graphics.ColorUtils
import android.util.TypedValue
import android.view.WindowManager
import java.util.*


fun <T> List<T?>.secondOrNull(): T? = if (size > 1) get(1) else null

fun Bitmap.extractDominantSwatch(): Maybe<Palette.Swatch> {
    return Maybe.create<Palette.Swatch> { emitter ->
        if (isRecycled) {
            emitter.onComplete()
        } else {
            Palette.from(this).generate { palette ->
                val swatch = palette.dominantSwatch
                if (swatch == null) {
                    emitter.onComplete()
                } else {
                    emitter.onSuccess(swatch)
                }
            }
        }
    }
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

fun adjustColorBrightness(color: Int, brightness: Float) = if (brightness in 0F..1F) {
    val hsv = FloatArray(3)
    Color.colorToHSV(color, hsv)
    hsv[2] = brightness
    Color.HSVToColor(1, hsv)
} else {
    throw IllegalArgumentException("Brightness parameter must be a value between 0F and 1F")
}

fun getNumberOfColumns(context: Context, columnWidth: Int): Int {
    val metrics = context.resources.displayMetrics
    val width = metrics.widthPixels / metrics.density
    return width.toInt() / columnWidth
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

fun ClosedRange<Int>.random() = Random().nextInt((endInclusive + 1) - start) +  start
