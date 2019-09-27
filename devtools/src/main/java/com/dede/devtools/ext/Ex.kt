package com.dede.devtools.ext

import android.content.Context
import android.text.TextUtils

/**
 * Created by hsh on 2019-09-26 20:05
 */

fun Context.dip(dp: Float): Int {
    return (this.resources.displayMetrics.density * dp + .5).toInt()
}

fun Context.dip(dp: Int): Int {
    return (this.resources.displayMetrics.density * dp + .5).toInt()
}

fun Context.screenWidth(): Int {
    return this.resources.displayMetrics.widthPixels
}

fun Context.screenHeight(): Int {
    return this.resources.displayMetrics.heightPixels
}

fun CharSequence?.isNull(): Boolean {
    return TextUtils.isEmpty(this)
}

fun CharSequence?.notNull(): Boolean {
    return !TextUtils.isEmpty(this)
}