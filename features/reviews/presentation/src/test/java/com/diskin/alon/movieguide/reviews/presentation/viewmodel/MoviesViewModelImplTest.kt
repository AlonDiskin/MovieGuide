package com.diskin.alon.movieguide.reviews.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import com.diskin.alon.movieguide.common.appservices.UseCase
import com.diskin.alon.movieguide.common.localtesting.WhiteBox
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.reviews.appservices.data.MovieDto
import com.diskin.alon.movieguide.reviews.appservices.data.MovieSorting
import com.diskin.alon.movieguide.reviews.appservices.data.SortedMoviesRequest
import com.diskin.alon.movieguide.reviews.presentation.data.Movie
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test

/**
 * [MoviesViewModelImpl] unit test class.
 */
class MoviesViewModelImplTest {

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
    private lateinit var viewModel: MoviesViewModelImpl

    // Collaborators
    private val getMoviesUseCase:  UseCase<SortedMoviesRequest, Observable<PagingData<MovieDto>>> = mockk()
    private val pagingMapper: Mapper<PagingData<MovieDto>, PagingData<Movie>> = mockk()
    private val savedState: SavedStateHandle = SavedStateHandle()

    // Stub data
    private val pagingDataSubject = PublishSubject.create<PagingData<MovieDto>>()

    // Capture args
    private val useCaseRequestSlot = slot<SortedMoviesRequest>()

    @Before
    fun setUp() {
        every { getMoviesUseCase.execute(capture(useCaseRequestSlot)) } returns pagingDataSubject

        viewModel = MoviesViewModelImpl(getMoviesUseCase,pagingMapper,savedState)
    }

    @Test
    fun initSelectedSortingStateAsPopularWhenCreatedWithoutPrevSortingState() {
        // Given an initialized view model created without saved sorting state

        // Then view model should set selected sorting state as popular
        @Suppress("UNCHECKED_CAST")
        val selectedSorting = WhiteBox
            .getInternalState(viewModel,"selectedSorting") as BehaviorSubject<MovieSorting>

        assertThat(selectedSorting.value).isEqualTo(MovieSorting.POPULARITY)
    }

    @Test
    fun initSelectedSortingStateAsSavedWhenCreatedWithSavedSortingState() {
        // Given an initialized view model created with saved sorting state
        val savedStateHandle = SavedStateHandle()
        val key = WhiteBox.getInternalState(viewModel,"KEY_SORTING") as String

        savedStateHandle.set(key, MovieSorting.RELEASE_DATE.name)
        viewModel = MoviesViewModelImpl(getMoviesUseCase,pagingMapper,savedStateHandle)

        // Then view model should set sorting state as popular
        @Suppress("UNCHECKED_CAST")
        val selectedSorting = WhiteBox
            .getInternalState(viewModel,"selectedSorting") as BehaviorSubject<MovieSorting>

        assertThat(selectedSorting.value).isEqualTo(MovieSorting.RELEASE_DATE)
    }

    @Test
    fun observeModelMoviesPagingWhenCreated() {
        // Given an initialized view model that has no saved sorting state

        // Then view model should subscribe to movies paging use case selected sorting state,
        // with paging config page size matching
        @Suppress("UNCHECKED_CAST")
        val selectedSorting =
            WhiteBox.getInternalState(viewModel,"selectedSorting") as BehaviorSubject<MovieSorting>
        val pageSize =
            WhiteBox.getInternalState(viewModel,"MOVIES_PAGE_SIZE") as Int

        verify { getMoviesUseCase.execute(any()) }
        assertThat(useCaseRequestSlot.captured.sorting).isEqualTo(selectedSorting.value)
        assertThat(useCaseRequestSlot.captured.config.pageSize).isEqualTo(pageSize)

        // And add subscription to disposable container
        val disposable =
            WhiteBox.getInternalState(viewModel,"container") as CompositeDisposable
        assertThat(disposable.size()).isEqualTo(1)
    }

    @Test
    fun updateMoviesPagingAndCurrentSortingStateWhenModelMoviesPagingUpdates() {
        // Test case fixture
        val moviesPaging: PagingData<Movie> = PagingData.empty()

        every { pagingMapper.map(any()) } returns moviesPaging

        // Given an initialized view model that subscribed to use case that emits movies dto paging
        // sorted by selected sorting

        // When use case emit paging
        val useCasePaging = PagingData.empty<MovieDto>()
        pagingDataSubject.onNext(useCasePaging)

        // Then view model should ask headlines mapper to map use case dto paging
        verify { pagingMapper.map(any()) }

        // And update its live data movies paging and current sorting state accordingly
        @Suppress("UNCHECKED_CAST")
        val selectedSorting =
            WhiteBox.getInternalState(viewModel,"selectedSorting") as BehaviorSubject<MovieSorting>

        assertThat(viewModel.movies.value).isEqualTo(moviesPaging)
        assertThat(viewModel.sorting.value).isEqualTo(selectedSorting.value)
    }

    @Test
    fun saveGivenSortingArgWhenAskedToSort() {
        // Given an initialized view model

        // When view model is asked to sort movies paging
        val sorting = MovieSorting.RATING
        viewModel.sortMovies(sorting)

        // Then view model should save passed arg
        val key = WhiteBox.getInternalState(viewModel,"KEY_SORTING") as String
        val actual = MovieSorting.valueOf(savedState.get<String>(key)!!)
        assertThat(actual).isEqualTo(sorting)
    }

    @Test
    fun changeSelectedSortingWhenAskedToSort() {
        // Given an initialized view model

        // When view model is asked to sort movies paging
        val sorting = MovieSorting.RELEASE_DATE
        viewModel.sortMovies(sorting)

        // Then view model should update selected sorting value
        @Suppress("UNCHECKED_CAST")
        val selectedSorting =
            WhiteBox.getInternalState(viewModel,"selectedSorting") as BehaviorSubject<MovieSorting>

        assertThat(selectedSorting.value).isEqualTo(sorting)

        // And should execute get movies use case to fetch new sorting observable, according to
        // asked sorting
        verify { getMoviesUseCase.execute(any()) }
        assertThat(useCaseRequestSlot.captured.sorting).isEqualTo(selectedSorting.value)
    }
}