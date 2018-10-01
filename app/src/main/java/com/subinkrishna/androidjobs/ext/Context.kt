@file:Suppress("NOTHING_TO_INLINE")
package com.subinkrishna.androidjobs.ext

import android.content.Context
import android.util.TypedValue

inline fun Context.px(dp: Float): Float {
  return TypedValue.applyDimension(
          TypedValue.COMPLEX_UNIT_DIP,
          dp,
          resources.displayMetrics)
}