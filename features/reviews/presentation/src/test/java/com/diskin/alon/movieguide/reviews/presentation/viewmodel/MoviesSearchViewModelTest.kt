package com.diskin.alon.movieguide.reviews.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import com.diskin.alon.movieguide.common.presentation.Model
import com.diskin.alon.movieguide.common.presentation.ModelRequest
import com.diskin.alon.movieguide.reviews.presentation.createMovies
import com.diskin.alon.movieguide.reviews.presentation.data.Movie
import com.diskin.alon.movieguide.reviews.presentation.data.SearchMoviesModelRequest
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test

/**
 * [MoviesSearchViewModel] unit test class.
 */
class MoviesSearchViewModelTest {

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
    private lateinit var viewModel: MoviesSearchViewModel

    // Collaborators
    private val model: Model = mockk()
    private val savedStateHandle = SavedStateHandle()

    // Stub data
    private val searchResultsSubject = BehaviorSubject.create<PagingData<Movie>>()
    private val modelRequestSlot = slot<ModelRequest<*,*>>()

    @Before
    fun setUp() {
        // Stub collaborators
        every { model.execute(capture(modelRequestSlot)) } returns searchResultsSubject

        viewModel = MoviesSearchViewModel(model,savedStateHandle)
    }

    @Test
    fun fetchSearchMovieFromModelWhenSearched() {
        // Given

        // When
        val query = "query"
        viewModel.search(query)

        // Then
        assertThat(modelRequestSlot.captured).isInstanceOf(SearchMoviesModelRequest::class.java)
        assertThat((modelRequestSlot.captured as SearchMoviesModelRequest).query).isEqualTo(query)
        assertThat((modelRequestSlot.captured as SearchMoviesModelRequest).config.pageSize)
            .isEqualTo(MoviesSearchViewModel.RESULTS_PAGE_SIZE)

        // When
        val paging = PagingData.from(createMovies())
        searchResultsSubject.onNext(paging)

        // Then
        assertThat(viewModel.results.value).isInstanceOf(PagingData::class.java)
    }

    @Test
    fun restoreSearchResultsWhenCreatedWithPrevState() {
        // Given
        val query = "query"
        savedStateHandle.set<String>(MoviesSearchViewModel.SEARCH_QUERY_KEY,query)
        viewModel = MoviesSearchViewModel(model,savedStateHandle)

        // Then
        assertThat(modelRequestSlot.captured).isInstanceOf(SearchMoviesModelRequest::class.java)
        assertThat((modelRequestSlot.captured as SearchMoviesModelRequest).query).isEqualTo(query)
        assertThat((modelRequestSlot.captured as SearchMoviesModelRequest).config.pageSize)
            .isEqualTo(MoviesSearchViewModel.RESULTS_PAGE_SIZE)
    }

    @Test
    fun storeSearchQueryStateForViewRestorationWhenSearched() {
        // Given

        // When
        val query = "query"
        viewModel.search(query)

        // Then
        assertThat(savedStateHandle.get<String>(MoviesSearchViewModel.SEARCH_QUERY_KEY)).isEqualTo(query)
    }

    @Test
    fun initializeEmptySearchTextWhenCreatedWithoutPrevState() {
        // Given

        // Then
        assertThat(viewModel.searchText).isEmpty()
    }

    @Test
    fun restoreSearchTextWhenCreatedWithPrevState() {
        // Given
        val text = "text"
        savedStateHandle.set<String>(MoviesSearchViewModel.SEARCH_TXT_KEY,text)
        viewModel = MoviesSearchViewModel(model,savedStateHandle)

        // Then
        assertThat(viewModel.searchText).isEqualTo(text)
    }

    @Test
    fun storeSearchTextStateForViewRestorationWhenChanged() {
        // Given

        // When
        val text = "text"
        viewModel.searchText = text

        // Then
        assertThat(savedStateHandle.get<String>(MoviesSearchViewModel.SEARCH_TXT_KEY)).isEqualTo(text)
    }
}