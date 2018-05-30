package com.rx.ugnius.rx.artist.view.utility

import android.content.Context
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_DIP

class ViewUtils private constructor() {
    companion object {
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

    }
}