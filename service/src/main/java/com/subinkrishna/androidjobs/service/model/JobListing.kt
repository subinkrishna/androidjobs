package com.subinkrishna.androidjobs.service.model

data class JobListing(
        val id: String,
        val company: String,
        val logoUrl: String? = "",
        val title: String,
        val type: String,
        val location: String,
        val tags: String? = "",
        val description: String? = "",
        val applicationUrl: String? = ""
) {
    override fun toString(): String {
        return "$id: $company: $title"
    }
}