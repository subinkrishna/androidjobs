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
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.subinkrishna.androidjobs.R
import com.subinkrishna.androidjobs.service.model.JobListing
import com.subinkrishna.ext.view.setImageUrl

class JobDetailsView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val logo: ImageView
    private val companyNameText: TextView
    private val jobTitleText: TextView
    private val jobTypeText: TextView
    private val locationText: TextView
    private val descriptionText: TextView
    private val applyButton: Button
    private var onApplyAction: ((url: String?) -> Unit)? = null
    private var jobListingItem: JobListing? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_job_details, this, true)
        logo = findViewById(R.id.logo)
        companyNameText = findViewById(R.id.companyNameText)
        jobTitleText = findViewById(R.id.jobTitleText)
        jobTypeText = findViewById(R.id.jobTypeText)
        locationText = findViewById(R.id.locationText)
        descriptionText = findViewById<TextView>(R.id.descriptionText).apply {
            movementMethod = LinkMovementMethod()
        }
        applyButton = findViewById<Button>(R.id.applyButton).apply {
            setOnClickListener { onApplyAction?.invoke(jobListingItem?.applicationUrl) }
        }
    }

    fun bind(item: JobListing) {
        jobListingItem = item
        logo.apply {
            setImageUrl(item.logoUrl, errorDrawableRes = R.drawable.ic_company_logo_placeholder)
            contentDescription = item.company
        }
        companyNameText.text = item.company
        jobTitleText.text = context.getString(R.string.job_details_title, item.title, item.company)
        jobTypeText.text = context.getString(R.string.job_details_type_category, item.type, item.tags)
        locationText.text = item.location
        descriptionText.text = Html.fromHtml(item.description)
    }

    fun onApply(action: ((url: String?) -> Unit)?) {
        onApplyAction = action
    }
}