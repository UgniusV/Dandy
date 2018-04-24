package com.rx.ugnius.rx

import android.content.Context
import android.graphics.Bitmap
import android.support.v7.graphics.Palette
import io.reactivex.Maybe
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.util.DisplayMetrics
import android.content.Context.WINDOW_SERVICE
import android.support.v4.graphics.ColorUtils
import android.util.TypedValue
import android.view.WindowManager


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
    hsl[2] = lightness
    ColorUtils.HSLToColor(hsl)
} else {
    throw IllegalArgumentException("Lightness parameter must be a value between 0F and 1F")

}