package com.dandy.ugnius.dandy.utilities

import android.graphics.Color

object Drawables {

    fun lightenOrDarken(color: Int, fraction: Double): Int {
        return if (canLighten(color, fraction)) {
            lighten(color, fraction)
        } else {
            darken(color, fraction)
        }
    }

    private fun lighten(color: Int, fraction: Double): Int {
        var red = Color.red(color)
        var green = Color.green(color)
        var blue = Color.blue(color)
        red = lightenColor(red, fraction)
        green = lightenColor(green, fraction)
        blue = lightenColor(blue, fraction)
        val alpha = Color.alpha(color)
        return Color.argb(alpha, red, green, blue)
    }

    private fun darken(color: Int, fraction: Double): Int {
        var red = Color.red(color)
        var green = Color.green(color)
        var blue = Color.blue(color)
        red = darkenColor(red, fraction)
        green = darkenColor(green, fraction)
        blue = darkenColor(blue, fraction)
        val alpha = Color.alpha(color)

        return Color.argb(alpha, red, green, blue)
    }

    private fun canLighten(color: Int, fraction: Double): Boolean {
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return (canLightenComponent(red, fraction)
            && canLightenComponent(green, fraction)
            && canLightenComponent(blue, fraction))
    }

    private fun canLightenComponent(colorComponent: Int, fraction: Double): Boolean {
        val red = Color.red(colorComponent)
        val green = Color.green(colorComponent)
        val blue = Color.blue(colorComponent)
        return (red + red * fraction < 255
            && green + green * fraction < 255
            && blue + blue * fraction < 255)
    }

    private fun darkenColor(color: Int, fraction: Double): Int {
        return Math.max(color - color * fraction, 0.0).toInt()
    }

    private fun lightenColor(color: Int, fraction: Double): Int {
        return Math.min(color + color * fraction, 255.0).toInt()
    }

}
