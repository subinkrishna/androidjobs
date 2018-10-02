/**
 * Copyright (C) 2018 Subinkrishna Gopi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
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
