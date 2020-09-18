package com.diskin.alon.movieguide.news.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.diskin.alon.movieguide.common.appservices.UseCase
import com.diskin.alon.movieguide.news.appservices.HeadlineDto
import com.diskin.alon.movieguide.news.appservices.HeadlinesRequest
import com.diskin.alon.movieguide.news.presentation.MoviesHeadlinesViewModelImpl.Companion.PAGE_SIZE
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test

/**
 * [MoviesHeadlinesViewModelImpl] unit test class.
 */
class MoviesHeadlinesViewModelImplTest {

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

    // Subject under test
    private lateinit var viewModel: MoviesHeadlinesViewModelImpl

    // Collaborators
    private val useCase: UseCase<HeadlinesRequest,Observable<PagingData<HeadlineDto>>> = mockk()

    // Stub data
    private val pagingDataSubject = PublishSubject.create<PagingData<HeadlineDto>>()

    // Capture args
    private val slot = slot<HeadlinesRequest>()

    @Before
    fun setUp() {
        // Stub mocked collaborator
        every { useCase.execute(capture(slot)) } returns pagingDataSubject

        // Init subject
        viewModel = MoviesHeadlinesViewModelImpl(useCase)
    }

    @Test
    fun fetchNewsHeadlineWhenCreated() {
        // Test case fixture
        val newsHeadlines = createNewsHeadlines()
        val useCasePaging = PagingData.empty<HeadlineDto>()
        val newsHeadlinesPaging = PagingData.from(newsHeadlines)

        mockkStatic("com.diskin.alon.movieguide.news.presentation.MapperKt")
        every { mapDtoPagingToNewsHeadline(any()) } returns newsHeadlinesPaging

        // Given an initialized view model

        // Then view model subscribed to use case execution
        verify { useCase.execute(any()) }
        // Currently PagingConfig doe not implement equals(), so request verification
        // should be based on checking each field/property :(
        assertThat(slot.captured.pagingConfig.pageSize).isEqualTo(PAGE_SIZE)

        // When use case emit paging data
        pagingDataSubject.onNext(useCasePaging)

        // Then view model should map use case paging data
        verify { mapDtoPagingToNewsHeadline(any()) }

        // And update headlines live data value with mapped paging
        assertThat(viewModel.headlines.value).isEqualTo(newsHeadlinesPaging)
    }

    @Test
    fun freeResourcesWhenViewModelCleared() {
        // Given an initialized view model

        // When view model is cleared
        val method = ViewModel::class.java.getDeclaredMethod("onCleared")
        method.isAccessible = true
        method.invoke(viewModel)

        // Then all rx subscriptions should be disposed by view model
        val field = MoviesHeadlinesViewModelImpl::class.java.getDeclaredField("disposable")
        field.isAccessible = true
        val disposable = field.get(viewModel) as Disposable

        assertThat(disposable.isDisposed).isTrue()
    }
}