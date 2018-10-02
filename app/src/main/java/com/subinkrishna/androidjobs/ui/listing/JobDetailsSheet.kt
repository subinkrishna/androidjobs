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
package com.subinkrishna.androidjobs.ui.listing

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import com.subinkrishna.androidjobs.R
import com.subinkrishna.androidjobs.ext.px
import com.subinkrishna.androidjobs.service.model.JobListing

class JobDetailsSheet @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val bottomSheetCloseButton: LinearLayout
    private val bottomSheetCloseCaption: TextView
    private val scrollContainer: NestedScrollView
    private val jobDetailsView: JobDetailsView
    private var onCloseAction: (() -> Unit)? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_job_details_sheet, this, true)
        jobDetailsView = findViewById(R.id.jobDetailsView)
        scrollContainer = findViewById<NestedScrollView>(R.id.scrollContainer).apply {
            setOnScrollChangeListener(::onContainerScroll)
        }

        bottomSheetCloseCaption = findViewById(R.id.bottomSheetCloseCaption)
        bottomSheetCloseButton = findViewById<LinearLayout>(R.id.bottomSheetCloseButton).apply {
            layoutTransition.setDuration(200)
            setOnClickListener { onCloseAction?.invoke() }
        }
    }

    override fun scrollTo(x: Int, y: Int) {
        scrollContainer.scrollTo(x, y)
    }

    override fun canScrollVertically(direction: Int): Boolean {
        return scrollContainer.canScrollVertically(direction)
    }

    fun bind(item: JobListing, scrollToTop: Boolean = true) {
        jobDetailsView.bind(item)
        if (scrollToTop) {
            scrollTo(0, 0)
        }
    }

    fun onClose(action: (() -> Unit)?) {
        onCloseAction = action
    }

    fun onApply(action: ((url: String?) -> Unit)?) {
        jobDetailsView.onApply(action)
    }

    fun showCloseButton(show: Boolean, animate: Boolean = false) {
        if (animate && show) {
            if (!bottomSheetCloseButton.isVisible) {
                bottomSheetCloseButton.apply {
                    isVisible = true
                    alpha = 0f
                    animate().alpha(1f).setDuration(200).start()
                }
            }
        } else {
            bottomSheetCloseButton.isVisible = show
        }
    }

    private fun onContainerScroll(
            v: NestedScrollView?,
            scrollX: Int,
            scrollY: Int,
            oldScrollX: Int,
            oldScrollY: Int
    ) {
        bottomSheetCloseButton.elevation = context.px(if (scrollY > 0) 3f else 0f)
        bottomSheetCloseCaption.isVisible = scrollY > 0
    }
}