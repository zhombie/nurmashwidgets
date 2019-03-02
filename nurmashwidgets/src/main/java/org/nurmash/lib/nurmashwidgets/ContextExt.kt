package org.nurmash.lib.nurmashwidgets

import android.content.Context
import android.util.TypedValue
import android.view.View


fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun dp2pixels(context: Context, dp: Float): Int {
    val displayMetrics = context.resources.displayMetrics
    return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics))
}

