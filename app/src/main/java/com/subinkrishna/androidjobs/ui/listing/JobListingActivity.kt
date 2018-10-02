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

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.subinkrishna.androidjobs.BaseActivity
import com.subinkrishna.androidjobs.R
import com.subinkrishna.androidjobs.ext.isExpanded
import com.subinkrishna.androidjobs.ext.isExpandedOrPeeked
import com.subinkrishna.androidjobs.service.model.JobListing


/**
 * Main activity that lists the jobs.
 *
 * @author Subinkrishna Gopi
 * @see JobListingViewModel
 */
class JobListingActivity : BaseActivity() {

    private val viewModel by lazy {
        val factory = ViewModelProvider.AndroidViewModelFactory
                .getInstance(application)
        ViewModelProviders.of(this, factory)[JobListingViewModel::class.java]
    }

    private lateinit var toolbarContainer: View
    private lateinit var progressContainer: View
    private lateinit var jobList: RecyclerView
    private lateinit var shutter: View
    private lateinit var jobDetailsSheet: JobDetailsSheet
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<JobDetailsSheet>

    private val jobListAdapter by lazy {
        JobListingAdapter(jobListingItemClickListener).apply {
            setHasStableIds(true)
        }
    }

    private val jobListingItemClickListener = View.OnClickListener { v ->
        val jobListing = v.tag as? JobListing ?: return@OnClickListener
        jobDetailsSheet.bind(jobListing)
        bottomSheetBehavior.isExpanded = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_listing)

        configureToolbar()

        toolbarContainer = findViewById(R.id.toolbarContainer)
        progressContainer = findViewById(R.id.progressIndicatorContainer)
        jobList = findViewById<RecyclerView>(R.id.jobList).apply {
            this.adapter = jobListAdapter
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(
                    this@JobListingActivity,
                    DividerItemDecoration.VERTICAL))
        }

        shutter = findViewById(R.id.shutter)
        jobDetailsSheet = findViewById<JobDetailsSheet>(R.id.bottomSheetContainer).apply {
            onClose { onBackPressed() }
            onApply { url ->
                url?.let {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent)
                    }
                }
            }
        }
        bottomSheetBehavior = BottomSheetBehavior.from(jobDetailsSheet).apply {
            setBottomSheetCallback(bottomSheetCallback)
            peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO
            skipCollapsed = true
            isExpanded = false
        }

        viewModel.viewState().observe(this, Observer {
            render(it)
        })
    }

    override fun onBackPressed() {
        when {
            bottomSheetBehavior.isExpandedOrPeeked() -> {
                bottomSheetBehavior.isExpanded = false
            }
            else -> super.onBackPressed()
        }
    }

    private fun configureToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar))
        findViewById<TextView>(R.id.toolbarTitleText).setText(R.string.app_name)
        setTitle(R.string.app_name)
        supportActionBar?.title = ""
    }

    private fun render(state: JobListingViewState) {
        val isLoading = state.isLoading
        val hasContent = state.content?.isNotEmpty() == true
        val hasError = state.error != null // todo: handle error cases

        progressContainer.isVisible = isLoading && !hasContent
        jobList.isVisible = hasContent

        if (hasContent) {
            jobListAdapter.submitList(state.content)
        }
    }

    /** Job details sheet callback */
    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            val sheetTop = bottomSheet.top
            // Fade in/out the shutter as sheet slides in/out
            shutter.apply {
                isVisible = true
                // Note: slideOffset doesn't seem to give the values needed to to
                // calculate the relative alpha
                alpha = 1f - (sheetTop.toFloat() / bottomSheet.height)
            }
            // Animate out the parent toolbar as bottom sheet slides in
            val toolbarHeight = toolbarContainer.height
            if (sheetTop <= toolbarHeight) {
                toolbarContainer.translationY = -(toolbarHeight - sheetTop).toFloat()
            } else {
                toolbarContainer.translationY = 0f
            }
        }

        @SuppressLint("SwitchIntDef")
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                jobDetailsSheet.showCloseButton(show = true, animate = true)
                shutter.isVisible = false
                jobList.isVisible = false
            } else {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    shutter.isVisible = false
                    jobDetailsSheet.showCloseButton(false)
                }
                jobList.isVisible = true
            }
        }
    }
}
