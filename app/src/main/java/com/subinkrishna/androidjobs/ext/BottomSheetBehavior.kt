@file:Suppress("NOTHING_TO_INLINE")
package com.subinkrishna.androidjobs.ext

import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior

inline var BottomSheetBehavior<out View>.isExpanded: Boolean
    get() = state == BottomSheetBehavior.STATE_EXPANDED
    set(value) {
        state =  if (value) BottomSheetBehavior.STATE_EXPANDED else BottomSheetBehavior.STATE_HIDDEN
    }

inline fun BottomSheetBehavior<out View>.isExpandedOrPeeked(): Boolean {
    return (state == BottomSheetBehavior.STATE_HALF_EXPANDED) or
           (state == BottomSheetBehavior.STATE_EXPANDED)
}

inline fun BottomSheetBehavior<out View>.peek() {
    state = BottomSheetBehavior.STATE_HALF_EXPANDED
}
