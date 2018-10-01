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
import com.subinkrishna.ext.setImageUrl

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
        logo.setImageUrl(item.logoUrl) // todo: set error drawable
        logo.contentDescription = item.company
        companyNameText.text = item.company
        jobTitleText.text = "${item.title} at ${item.company}"
        jobTypeText.text = item.type
        locationText.text = item.location
        descriptionText.text = Html.fromHtml(item.description)
    }

    fun onApply(action: ((url: String?) -> Unit)?) {
        onApplyAction = action
    }
}