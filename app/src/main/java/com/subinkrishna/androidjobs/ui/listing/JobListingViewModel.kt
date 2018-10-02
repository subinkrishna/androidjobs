package com.subinkrishna.androidjobs.ui.listing

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.subinkrishna.androidjobs.service.AndroidJobsApi
import com.subinkrishna.androidjobs.service.RetrofitAndroidJobsApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class JobListingViewModel(app: Application) : AndroidViewModel(app) {

    private var viewState = JobListingViewState(isLoading = true)
    private val viewStateLive = MutableLiveData<JobListingViewState>()
    private val disposable = CompositeDisposable()
    private val api: AndroidJobsApi = RetrofitAndroidJobsApi()

    init {
        viewStateLive.value = viewState
        fetchJobListing()
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }

    fun viewState(): LiveData<JobListingViewState> = viewStateLive

    private fun fetchJobListing() {
        val listingObservable = api.getJobs()
        disposable.add(listingObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { listing ->
                            viewStateLive.value = viewState.copy(
                                    isLoading = false,
                                    content = listing,
                                    error = null).also { viewState = it }
                        },
                        { e ->
                            Timber.e(e,"onError!")
                            viewStateLive.value = viewState.copy(
                                    isLoading = false,
                                    error = e).also { viewState = it }
                        }))
    }
}