package com.subinkrishna.androidjobs.ui.listing

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockito_kotlin.whenever
import org.mockito.Mockito.mock
import com.subinkrishna.androidjobs.AndroidJobsApp
import com.subinkrishna.androidjobs.service.AndroidJobsApi
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import org.junit.Test

class JobListingViewModelTest {

    private val mockApp: AndroidJobsApp by lazy { mock(AndroidJobsApp::class.java) }
    private val mockApi: AndroidJobsApi by lazy {
        mock(AndroidJobsApi::class.java).apply {
            whenever(getJobs()).thenReturn(Single.just(emptyList()))
        }
    }

    @Test
    fun onStart_shouldReceive() {
        val vm = JobListingViewModel(mockApp, mockApi)
        val fetchJobsEvent = PublishSubject.create<JobListingEvent.FetchJobsEvent>()
        val itemClickEvent = PublishSubject.create<JobListingEvent.ItemSelectEvent>()
        val remoteToggleEvent = PublishSubject.create<JobListingEvent.RemoteToggleEvent>()

        // val stateLive = vm.start(fetchJobsEvent, itemClickEvent, remoteToggleEvent)
        // assertThat(stateLive.value?.isLoading).isTrue()
        // assertThat(stateLive.value?.filter).isSameAs(Filter.All)
    }

}