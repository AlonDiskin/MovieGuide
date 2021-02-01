package com.diskin.alon.movieguide.news.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.diskin.alon.movieguide.common.localtesting.WhiteBox
import com.diskin.alon.movieguide.common.presentation.Model
import com.diskin.alon.movieguide.news.presentation.data.Headline
import com.diskin.alon.movieguide.news.presentation.data.HeadlinesModelRequest
import com.diskin.alon.movieguide.news.presentation.viewmodel.HeadlinesViewModel.Companion.PAGE_SIZE
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test

/**
 * [HeadlinesViewModel] unit test class.
 */
class HeadlinesViewModelTest {

    companion object {

        @JvmStatic
        @BeforeClass
        fun setupClass() {
            // Set Rx framework for testing
            RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        }
    }

    // Lifecycle testing rule
    @JvmField
    @Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Test subject
    private lateinit var viewModel: HeadlinesViewModel

    // Collaborators
    private val model: Model = mockk()

    // Stub data
    private val modelHeadlinesSubject = PublishSubject.create<PagingData<Headline>>()

    // Capture args
    private val modelRequestSlot = slot<HeadlinesModelRequest>()

    @Before
    fun setUp() {
//        mockkStatic("androidx.paging.rxjava2.PagingRxKt")
//
//        every { cachedIn(any()) }
        // Stub mocked collaborator
        every { model.execute(capture(modelRequestSlot)) } returns modelHeadlinesSubject

        // Init subject
        viewModel = HeadlinesViewModel(model)
    }

    @Test
    fun fetchModelHeadlinePagingWhenCreated() {
        // Given an initialized view model

        // Then view model should subscribe to model headlines paging with defined paged size
        val expectedModelRequest = HeadlinesModelRequest(PagingConfig(pageSize = PAGE_SIZE))
        verify { model.execute(any<HeadlinesModelRequest>()) }
        assertThat(modelRequestSlot.captured.pagingConfig.pageSize)
            .isEqualTo(expectedModelRequest.pagingConfig.pageSize)

        // And add subscription to disposable container
        val disposable = WhiteBox.getInternalState(viewModel,"container") as CompositeDisposable
        assertThat(disposable.size()).isEqualTo(1)
    }

    @Test
    fun updateHeadlinesPagingStateWhenModelPagingUpdates() {
        // Given an initialized view model that subscribed to use case that emits headlines dto paging

        // When model update headlines paging
        modelHeadlinesSubject.onNext(PagingData.empty())

        // Then view model should update view headlines paging state
        assertThat(viewModel.headlines.value).isNotNull()
        assertThat(viewModel.headlines.value).isInstanceOf(PagingData::class.java)
    }
}