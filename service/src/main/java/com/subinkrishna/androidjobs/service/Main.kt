@file:JvmName("Main")
package com.subinkrishna.androidjobs.service

internal fun main(args: Array<String>) {
    // [DONE] todo: use Retrofit to fetch the contents
    // [DONE] todo: Create Jsoup adapter for HTML parsing
    // todo: cache it
    TestAndroidJobsApi().getJobs().blockingGet().forEach {
        println("${it.title}")
    }
}
