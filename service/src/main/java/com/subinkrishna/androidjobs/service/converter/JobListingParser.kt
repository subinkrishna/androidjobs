package com.subinkrishna.androidjobs.service.converter

import com.subinkrishna.androidjobs.service.model.JobListing
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class JobListingParser {

    companion object {
        /**
         * Parses the HTML body to extract [JobListing]s
         *
         * @param body - HTML body
         * @return Job listings
         */
        fun parse(body: String): List<JobListing> = parse(Jsoup.parse(body))

        /**
         * Extracts the [JobListing]s from the [Document]
         *
         * @param doc
         * @return Job listings
         */
        fun parse(doc: Document): List<JobListing> {
            val contents = doc.getElementsByAttributeValue("data-w-tab", "All")
                    ?.asSequence()
                    ?.firstOrNull { it.tag().toString() == "div" }
            return contents
                    ?.select("div.w-dyn-list > div.w-dyn-items > div.w-dyn-item")
                    ?.mapNotNull { it.toJobListing() }
                    ?: emptyList()
        }

        /**
         * Extension function which converts an [Element] in to [JobListing]
         *
         * @return [JobListing]
         */
        private fun Element.toJobListing(): JobListing? {
            // Job card
            val jobCard = selectFirst("div.job-card")
            val jobInfo = jobCard?.selectFirst("div.job-info")
            val logoUrl = jobCard?.selectFirst("div.company-logo > img")?.attr("src")
            val title = jobInfo?.selectFirst("h1.heading")?.text()
            val company = jobInfo?.selectFirst("h2.heading-2")?.text()
            val location = jobCard?.selectFirst("div.tag-styles.location > div")?.text()
            // Full-time, part-time etc.
            val type = jobCard?.selectFirst("div.tag-styles.type > div")?.text()
            // Job category - Engineering etc.
            val tags = jobCard?.selectFirst("div.tag-styles.tag > div")?.text()

            // Job description card
            val jobDescriptionCard = selectFirst("div.job-card.details")
            val description = jobDescriptionCard?.selectFirst("div.rich-text-block.w-richtext")
            val applyUrl = jobDescriptionCard?.selectFirst("a")?.attr("href")

            return if (!company.isNullOrBlank() &&
                    !title.isNullOrBlank() &&
                    !location.isNullOrBlank()) {
                JobListing(
                        id = "${company}_${title}_${location}",
                        company = company!!,
                        logoUrl = logoUrl,
                        title = title!!,
                        type = type!!,
                        location = location!!,
                        tags = tags,
                        description = description?.html() ?: "",
                        applicationUrl = applyUrl)
            } else {
                null
            }
        }
    }
}