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
package com.subinkrishna.androidjobs.ui.widget

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior

/**
 * Touch intercepted [BottomSheetBehavior] that dismisses the bottom sheet
 * if the user taps outside its visible [Rect]
 *
 * @author Subinkrishna Gopi
 */
class TouchInterceptedBottomSheetBehavior<V : View> @JvmOverloads constructor(
        context: Context? = null,
        attrs: AttributeSet? = null
) : BottomSheetBehavior<V>(context, attrs) {

    override fun onInterceptTouchEvent(
            parent: CoordinatorLayout,
            child: V,
            event: MotionEvent
    ): Boolean {
        if ((event.action == MotionEvent.ACTION_DOWN) &&
                (state == BottomSheetBehavior.STATE_EXPANDED ||
                 state == BottomSheetBehavior.STATE_HALF_EXPANDED)) {
            val outRect = Rect()
            child.getGlobalVisibleRect(outRect)
            if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                state = BottomSheetBehavior.STATE_HIDDEN
            }
        }
        return super.onInterceptTouchEvent(parent, child, event)
    }
}