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
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.subinkrishna.androidjobs.R
import com.subinkrishna.androidjobs.ext.isExpanded
import com.subinkrishna.androidjobs.ext.isExpandedOrPeeked
import com.subinkrishna.androidjobs.service.model.JobListing
import com.subinkrishna.androidjobs.ui.listing.JobListingEvent.ItemSelectEvent
import com.subinkrishna.androidjobs.ui.listing.JobListingEvent.RemoteToggleEvent
import com.subinkrishna.androidjobs.ui.widget.DividerDecoration
import com.subinkrishna.ext.setGifResource
import io.reactivex.subjects.PublishSubject


/**
 * Main activity that lists the jobs.
 *
 * @author Subinkrishna Gopi
 * @see JobListingViewModel
 */
class JobListingActivity : AppCompatActivity() {

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
    private lateinit var statusImage: ImageView
    private lateinit var statusText: TextView
    private lateinit var remoteToggle: TextView

    private val itemSelectEvent = PublishSubject.create<ItemSelectEvent>()
    private val remoteToggleEvent = PublishSubject.create<RemoteToggleEvent>()

    private val jobListAdapter by lazy {
        val itemClickListener = View.OnClickListener { v ->
            val jobListing = v.tag as? JobListing ?: return@OnClickListener
            itemSelectEvent.onNext(ItemSelectEvent(jobListing))
        }
        JobListingAdapter(itemClickListener).apply {
            setHasStableIds(true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_listing)
        configureToolbar()
        configureUi(savedInstanceState)
        viewModel.start(itemSelectEvent, remoteToggleEvent).observe(this, Observer {
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

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putBoolean(KEY_IS_SHEET_EXPANDED, bottomSheetBehavior.isExpandedOrPeeked())
    }

    // Internal methods

    private fun configureToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar))
        findViewById<TextView>(R.id.toolbarTitleText).setText(R.string.app_name)
        setTitle(R.string.app_name)
        supportActionBar?.title = ""
    }

    private fun configureUi(savedInstanceState: Bundle? = null) {
        toolbarContainer = findViewById(R.id.toolbarContainer)
        progressContainer = findViewById(R.id.progressIndicatorContainer)
        shutter = findViewById(R.id.shutter)
        statusImage = findViewById(R.id.androidGifImage)
        statusText = findViewById(R.id.statusMessageText)
        remoteToggle = findViewById<TextView>(R.id.remoteToggle).apply {
            setOnClickListener { remoteToggleEvent.onNext(RemoteToggleEvent) }
            isVisible = false // Show it only after fetching the listing
        }

        jobList = findViewById<RecyclerView>(R.id.jobList).apply {
            this.adapter = jobListAdapter
            setHasFixedSize(true)
            addItemDecoration(DividerDecoration())
        }

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

        val isSheetExpanded = savedInstanceState?.getBoolean(KEY_IS_SHEET_EXPANDED) ?: false
        if (isSheetExpanded) {
            // Hide the toolbar if the sheet is expanded
            toolbarContainer.post {
                toolbarContainer.translationY = -toolbarContainer.height.toFloat()
            }
        }
    }

    private fun render(state: JobListingViewState) {
        val isLoading = state.isLoading
        val hasContent = state.content?.isNotEmpty() == true
        val hasError = state.error != null
        val hasItemInFocus = null != state.itemInFocus

        progressContainer.isVisible = isLoading && !hasContent
        jobList.isVisible = hasContent
        remoteToggle.isVisible = true
        remoteToggle.isSelected = state.filter == Filter.Remote
        statusImage.isVisible = !isLoading && !hasContent
        statusText.isVisible = !isLoading && !hasContent

        if (hasContent) {
            jobListAdapter.submitList(state.content)
            if (hasItemInFocus) {
                jobDetailsSheet.bind(state.itemInFocus!!)
                bottomSheetBehavior.isExpanded = true
            }
        } else if (!isLoading && !hasContent) {
            statusImage.setGifResource(R.raw.gif_androidify_basketball)
            statusText.setText(R.string.empty_no_listing)
        }
        else if (hasError) {
            statusImage.setGifResource(R.raw.gif_androidify_basketball)
            val errorMessage = if (isOnline())
                R.string.error_job_listing_unknown
            else R.string.error_job_listing_offline
            statusText.setText(errorMessage)
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
                    // Set "no item" selected
                    itemSelectEvent.onNext(ItemSelectEvent(null))
                }
                jobList.isVisible = true
            }
        }
    }

    private fun isOnline(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo?.isConnectedOrConnecting == true
    }

    companion object {
        private const val KEY_IS_SHEET_EXPANDED = "JobListingActivity.IsSheetExpanded"
    }
}
