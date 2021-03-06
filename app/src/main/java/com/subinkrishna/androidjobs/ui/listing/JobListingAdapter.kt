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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.subinkrishna.androidjobs.R
import com.subinkrishna.androidjobs.service.model.JobListing
import com.subinkrishna.ext.view.setImageUrl

class JobListingAdapter(
        private val itemClickListener: View.OnClickListener? = null
) : ListAdapter<JobListing, JobItemViewHolder>(JOB_LISTING_DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobItemViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.view_job_listing_item, parent, false)
        return JobItemViewHolder(v)
    }

    override fun onBindViewHolder(holder: JobItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener(itemClickListener)
        holder.itemView.tag = item
    }

    override fun getItemId(position: Int): Long {
        return when {
            position < itemCount -> getItem(position)?.id ?: ""
            else -> ""
        }.hashCode().toLong()
    }
}

class JobItemViewHolder(v: View) : RecyclerView.ViewHolder(v) {

    val logo: ImageView = itemView.findViewById(R.id.logo)
    val companyNameText: TextView = itemView.findViewById(R.id.companyNameText)
    val jobTitleText: TextView = itemView.findViewById(R.id.jobTitleText)
    val locationText: TextView = itemView.findViewById(R.id.locationText)

    fun bind(item: JobListing) {
        logo.apply {
            setImageUrl(item.logoUrl, errorDrawableRes = R.drawable.ic_company_logo_placeholder)
            contentDescription = item.company
        }
        companyNameText.text = item.company
        jobTitleText.text = item.title
        locationText.text = item.location
    }
}

val JOB_LISTING_DIFF = object : DiffUtil.ItemCallback<JobListing>() {
    override fun areItemsTheSame(oldItem: JobListing, newItem: JobListing): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: JobListing, newItem: JobListing): Boolean {
        return oldItem.id == newItem.id
    }
}