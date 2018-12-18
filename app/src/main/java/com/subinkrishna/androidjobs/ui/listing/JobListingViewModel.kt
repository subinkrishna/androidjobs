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

@file:Suppress("NOTHING_TO_INLINE")

package com.subinkrishna.androidjobs.ui.listing

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MutableLiveData
import com.subinkrishna.androidjobs.model.Lce
import com.subinkrishna.androidjobs.service.AndroidJobsApi
import com.subinkrishna.androidjobs.service.RetrofitAndroidJobsApi
import com.subinkrishna.androidjobs.service.model.JobListing
import com.subinkrishna.androidjobs.ui.listing.JobListingEvent.*
import com.subinkrishna.androidjobs.ui.listing.JobListingResult.*
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

/**
 * USF (Uni-directional State Flow) based ViewModel implementation for job listing.
 * Inspired by https://github.com/kaushikgopal/movies-usf/blob/master/app/src/main/java/co/kaush/msusf/movies/MSMovieVm.kt
 *
 * @author Subinkrishna Gopi
 */
class JobListingViewModel(val app: Application) : AndroidViewModel(app) {

    // Current view state
    private var viewState = JobListingViewState(isLoading = true)
    private val viewStateLive by lazy { MutableLiveData<JobListingViewState>() }

    // Holds all the job listings
    private var itemsLive = MutableLiveData<List<JobListing>>()

    // Holds current listing filter
    private var filter = Filter.All

    private val disposable by lazy { CompositeDisposable() }
    private val api: AndroidJobsApi by lazy { RetrofitAndroidJobsApi() }

    // Job fetch is not a user triggered event, hence moving it to ViewModel
    private var isJobFetchTriggered = false
    private val fetchJobsEvent: Observable<FetchJobsEvent>
        get() {
            // This event needs to be triggered only once
            return if (!isJobFetchTriggered) {
                isJobFetchTriggered = true
                Observable.just(FetchJobsEvent)
            } else {
                Observable.empty()
            }
        }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }

    fun start(
            itemClickEvent: Observable<ItemSelectEvent>,
            remoteToggleEvent: Observable<RemoteToggleEvent>
    ): LiveData<out JobListingViewState> {
        // Merge events and get results
        val results: Observable<Lce<out JobListingResult>> = Observable.merge(
                fetchJobsEvent.compose(onFetchJobs()),
                remoteToggleEvent.compose(onRemoteToggle()),
                itemClickEvent.compose(onJobItemClick()))

        // Reduce to state & update LiveData
        disposable.add(reduceResultToViewState(results)
                .distinctUntilChanged()
                .log("State")
                .doOnNext {
                    viewState = it
                    viewStateLive.postValue(it)
                }
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe())

        return viewStateLive
    }


    // Internal methods

    private fun onFetchJobs(): ObservableTransformer<FetchJobsEvent, Lce<FetchJobsResult>> {
        return ObservableTransformer { upstream ->
            upstream.switchMap { _ ->
                api.getJobs().toObservable()
                        .doOnNext { itemsLive.postValue(it) }
                        .map { FetchJobsResult(items = it) }
                        .onErrorReturn { FetchJobsResult(error = it) }
                        .map { if (it.error != null) Lce.Error(it) else Lce.Content(it) }
                        .startWith(Lce.Loading())
            }
        }
    }

    private fun onRemoteToggle(): ObservableTransformer<RemoteToggleEvent, Lce<FilteredListingResult>> {
        return ObservableTransformer { upstream ->
            upstream.map { _ ->
                filter = if (filter == Filter.All) Filter.Remote else Filter.All
                val filteredItems = when (filter) {
                    Filter.All -> itemsLive.value
                    Filter.Remote -> {
                        itemsLive.value?.filter { it.location.toLowerCase().contains("remote") }
                    }
                }
                Lce.Content(FilteredListingResult(filteredItems))
            }
        }
    }

    private fun onJobItemClick(): ObservableTransformer<ItemSelectEvent, Lce<ItemSelectResult>> {
        return ObservableTransformer { upstream ->
            upstream.map {
                Lce.Content(ItemSelectResult(it.item))
            }
        }
    }

    private fun reduceResultToViewState(
            results: Observable<Lce<out JobListingResult>>
    ): Observable<JobListingViewState> {
        return results.scan(viewState) { viewState, result ->
            Timber.d("result: $result")
            when (result) {
                is Lce.Loading -> {
                    viewState.copy(isLoading = true, error = null)
                }
                is Lce.Content -> {
                    when (result.payload) {
                        is FetchJobsResult -> {
                            viewState.copy(
                                    isLoading = false,
                                    error = null,
                                    content = result.payload.items)
                        }
                        is FilteredListingResult -> {
                            viewState.copy(
                                    content = result.payload.items)
                        }
                        is ItemSelectResult -> {
                            viewState.copy(itemInFocus = result.payload.item)
                        }
                    }
                }
                is Lce.Error -> {
                    when (result.payload) {
                        is FetchJobsResult -> {
                            viewState.copy(isLoading = false, error = result.payload.error)
                        }
                        else -> { throw IllegalStateException("Something went wrong!") }
                    }
                }
            }
        }
    }


    // Internal extension functions

    /**
     * Extension function that logs all the items emitted by an [Observable] with a prefix.
     *
     * Note: For testing purpose only!
     * TODO: Remove after testing
     */
    private inline fun <reified T> Observable<T>.log(prefix: String? = null): Observable<T> {
        return doOnNext { Timber.i("$prefix: $it") }
    }

    /**
     * Extension function that converts an [Observable] into a [LiveData]
     */
    private inline fun <T> Observable<T>.liveData(): LiveData<T> {
        return LiveDataReactiveStreams.fromPublisher(toFlowable(BackpressureStrategy.LATEST))
    }
}