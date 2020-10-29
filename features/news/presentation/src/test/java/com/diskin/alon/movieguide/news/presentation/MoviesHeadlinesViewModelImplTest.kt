package com.diskin.alon.movieguide.news.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingData
import com.diskin.alon.movieguide.common.appservices.UseCase
import com.diskin.alon.movieguide.common.common.Mapper
import com.diskin.alon.movieguide.common.presentation.RxViewModel
import com.diskin.alon.movieguide.news.appservices.model.HeadlineDto
import com.diskin.alon.movieguide.news.appservices.model.HeadlinesRequest
import com.diskin.alon.movieguide.news.presentation.model.NewsHeadline
import com.diskin.alon.movieguide.news.presentation.viewmodel.MoviesHeadlinesViewModelImpl
import com.diskin.alon.movieguide.news.presentation.viewmodel.MoviesHeadlinesViewModelImpl.Companion.PAGE_SIZE
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.disposables.CompositeDisposable
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
    private val pagingMapper: Mapper<PagingData<HeadlineDto>, PagingData<NewsHeadline>> = mockk()

    // Stub data
    private val pagingDataSubject = PublishSubject.create<PagingData<HeadlineDto>>()
    private val headlinesPaging: PagingData<NewsHeadline> = PagingData.empty()

    // Capture args
    private val useCaseRequestSlot = slot<HeadlinesRequest>()

    @Before
    fun setUp() {
        // Stub mocked collaborator
        every { useCase.execute(capture(useCaseRequestSlot)) } returns pagingDataSubject
        every { pagingMapper.map(any()) } returns headlinesPaging


        // Init subject
        viewModel = MoviesHeadlinesViewModelImpl(useCase,pagingMapper)
    }

    @Test
    fun observeModelHeadlinePagingWhenCreated() {
        // Given an initialized view model

        // Then view model should subscribe to headlines use case paging with const page size
        verify { useCase.execute(any()) }
        // Currently PagingConfig doe not implement equals(), so request verification
        // should be based on checking each field/property :(
        assertThat(useCaseRequestSlot.captured.pagingConfig.pageSize).isEqualTo(PAGE_SIZE)

        // And add subscription to disposable container
        val field = RxViewModel::class.java.getDeclaredField("disposable")
        field.isAccessible = true
        val disposable = field.get(viewModel) as CompositeDisposable
        assertThat(disposable.size()).isEqualTo(1)
    }

    @Test
    fun updateHeadlinesPagingStateWhenModelPagingUpdates() {
        // Given an initialized view model that subscribed to use case that emits headlines dto paging

        // When use case emit paging
        val useCasePaging = PagingData.empty<HeadlineDto>()
        pagingDataSubject.onNext(useCasePaging)

        // Then view model should ask headlines mapper to map use case dto paging
        verify { pagingMapper.map(any()) }

        // And update its live data headlines paging state
        assertThat(viewModel.headlines.value).isEqualTo(headlinesPaging)
    }
}