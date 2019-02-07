package org.nurmash.lib.nurmashwidgets

import android.app.Activity
import android.content.Context
import android.util.TypedValue
import android.view.View
import androidx.fragment.app.Fragment

fun Activity.simpleClassName(): String = this::class.java.simpleName

fun Fragment.simpleClassName(): String = this::class.java.simpleName

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

val Fragment.ctx: Context
    get() = requireContext()