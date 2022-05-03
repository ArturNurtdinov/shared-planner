package ru.spbstu.common.extensions

import android.content.Context
import android.content.res.Resources

public fun Context.getStatusBarHeight(): Int {
    var result = 0
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = resources.getDimensionPixelSize(resourceId)
    }
    return result
}

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()
val Int.px: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()