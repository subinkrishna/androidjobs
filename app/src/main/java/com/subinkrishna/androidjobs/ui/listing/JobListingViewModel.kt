/**
 * Copyright (C) 2019 Subinkrishna Gopi
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
import com.subinkrishna.androidjobs.service.model.JobListing
import com.subinkrishna.androidjobs.ui.listing.JobListingEvent.*
import com.subinkrishna.androidjobs.ui.listing.JobListingResult.*
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

/**
 * USF (Uni-directional State Flow) based ViewModel implementation for job listing.
 * Inspired by https://github.com/kaushikgopal/movies-usf/blob/master/app/src/main/java/co/kaush/msusf/movies/MSMovieVm.kt
 */
class JobListingViewModel(
        private val app: Application,
        private val api: AndroidJobsApi,
        private val autoFetch: Boolean = true
) : AndroidViewModel(app) {

    // View state observables
    private val viewStateObservable: BehaviorSubject<JobListingViewState> = BehaviorSubject.create()
    private val viewStateLive: MutableLiveData<JobListingViewState> by lazy {
        MutableLiveData<JobListingViewState>()
    }

    // Result stream
    private val results: PublishSubject<Lce<out JobListingResult>> = PublishSubject.create()

    // Input event streams
    private val fetchJobsEvent: PublishSubject<FetchJobsEvent> = PublishSubject.create()
    private val itemClickEvent: PublishSubject<ItemSelectEvent> = PublishSubject.create()
    private val remoteToggleEvent: PublishSubject<RemoteToggleEvent> = PublishSubject.create()

    // Disposable
    private val disposable by lazy { CompositeDisposable() }

    // Holds all the job listings
    private var itemsLive = MutableLiveData<List<JobListing>>()

    // Holds current listing filter
    private var filter = Filter.All

    init {
        results.publish().apply {
            compose(resultToViewState()).subscribe(viewStateObservable)
            autoConnect(0) { disposable.add(it) }
        }

        disposable.add(startWith(
                fetchJobsEvent,
                itemClickEvent.distinctUntilChanged(),
                remoteToggleEvent))
        disposable.add(viewState()
                .doOnNext {
                    Timber.d("==> $it")
                    viewStateLive.postValue(it)
                }
                .subscribe())

        if (autoFetch) {
            fetchJobsEvent.onNext(FetchJobsEvent)
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }

    @SuppressWarnings("WeakerAccess")
    fun startWith(
            fetchJobsEvent: Observable<FetchJobsEvent>,
            itemClickEvent: Observable<ItemSelectEvent>,
            remoteToggleEvent: Observable<RemoteToggleEvent>
    ): Disposable {
        // Merge events and get results
        val results: Observable<Lce<out JobListingResult>> = Observable.merge(
                fetchJobsEvent, remoteToggleEvent , itemClickEvent)
                .doOnNext { Timber.d("<-- $it") }
                .compose { event ->
                    event.publish {
                        Observable.merge(
                                it.ofType(FetchJobsEvent::class.java).compose(onFetchJobs()),
                                it.ofType(RemoteToggleEvent::class.java).compose(onRemoteToggle()),
                                it.ofType(ItemSelectEvent::class.java).compose(onJobItemClick())
                        )
                    }
                }
                .doOnNext { Timber.d("--> $it") }
        // Publish each result to result stream
        return results
                .doOnNext { this.results.onNext(it) }
                .subscribe()
    }

    @SuppressWarnings("unused", "WeakerAccess")
    fun viewStateLive(): LiveData<out JobListingViewState> = viewStateLive

    @SuppressWarnings("unused", "WeakerAccess")
    fun viewState(): Observable<JobListingViewState> = viewStateObservable

    // todo: bad design. switch to filterBy()
    fun toggle() {
        remoteToggleEvent.onNext(RemoteToggleEvent)
    }

    fun select(item: JobListing?) {
        itemClickEvent.onNext(ItemSelectEvent(item))
    }


    // Internal methods

    private fun onFetchJobs(): ObservableTransformer<FetchJobsEvent, Lce<FetchJobsResult>> {
        return ObservableTransformer { upstream ->
            upstream.switchMap {
                api.getJobs().toObservable()
                        .subscribeOn(Schedulers.io())
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
            upstream
                    .takeWhile { itemsLive.value?.isNotEmpty() == true }
                    .map {
                        filter = if (filter == Filter.All) Filter.Remote else Filter.All
                        val filteredItems = when (filter) {
                            Filter.All -> itemsLive.value
                            Filter.Remote -> {
                                itemsLive.value?.filter { it.location.toLowerCase().contains("remote") }
                            }
                        }
                        Lce.Content(FilteredListingResult(items = filteredItems, filter = filter))
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

    private fun resultToViewState(): ObservableTransformer<Lce<out JobListingResult>, JobListingViewState> {
        return ObservableTransformer { resultStream ->
            val startState = viewStateObservable.value
                    ?: JobListingViewState(isLoading = true, filter = Filter.All)
            resultStream.scan(startState) { viewState, result ->
                when (result) {
                    is Lce.Loading -> viewState.copy(isLoading = true, error = null)
                    is Lce.Content -> {
                        when (result.payload) {
                            is FetchJobsResult -> viewState.copy(isLoading = false,
                                    error = null, content = result.payload.items)
                            is FilteredListingResult -> viewState.copy(content = result.payload.items,
                                    filter = result.payload.filter)
                            is ItemSelectResult -> viewState.copy(itemInFocus = result.payload.item)
                        }
                    }
                    is Lce.Error -> {
                        when (result.payload) {
                            is FetchJobsResult -> viewState.copy(isLoading = false, error = result.payload.error)
                            else -> throw IllegalStateException("Something went wrong!")
                        }
                    }
                }
            }.distinctUntilChanged()
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