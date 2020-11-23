package com.diskin.alon.movieguide.news.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.localtesting.WhiteBox
import com.diskin.alon.movieguide.common.presentation.Model
import com.diskin.alon.movieguide.common.presentation.ViewData
import com.diskin.alon.movieguide.news.appservices.data.BookmarkSorting
import com.diskin.alon.movieguide.news.presentation.createNewsHeadlines
import com.diskin.alon.movieguide.news.presentation.data.BookmarksModelRequest
import com.diskin.alon.movieguide.news.presentation.data.Headline
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test

/**
 * [BookmarksViewModelImpl] unit test class.
 */
class BookmarksViewModelImplTest {

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
    private lateinit var viewModel: BookmarksViewModel

    // Collaborators
    private val model: Model = mockk()
    private val savedStateHandle = SavedStateHandle()

    // Stub data
    private val modelBookmarksSubject =
        PublishSubject.create<Result<List<Headline>>>()

    @Before
    fun setUp() {
        // Stub collaborators
        every { model.execute(any<BookmarksModelRequest>()) } returns modelBookmarksSubject

        // Init subject
        viewModel = BookmarksViewModelImpl(model,savedStateHandle)
    }

    @Test
    fun initBookmarksViewStateAsUpdatingWhenCreated() {
        // Given an initialized view model

        // Then view model should have initialized sorting view state as updating
        assertThat(viewModel.bookmarks.value).isInstanceOf(ViewData.Updating::class.java)
    }

    @Test
    fun initDefaultSelectedSortingWhenCreatedWithoutSavedSorting() {
        // Given an initialized view model that was created without saved sorting state

        // Then view model selected sorting state should be set as its default
        val defaultSorting =
            WhiteBox.getInternalState(viewModel,"DEFAULT_SORTING") as BookmarkSorting
        val selectedSorting =
            WhiteBox.getInternalState(viewModel,"selectedSorting") as BehaviorSubject<BookmarkSorting>

        assertThat(selectedSorting.value).isEqualTo(defaultSorting)
    }

    @Test
    fun restoreSelectedSortingWhenCreatedWithSavedSorting() {
        // Given an initialized view model that was created with saved sorting value
        savedStateHandle["sorting"] = BookmarkSorting.OLDEST
        viewModel = BookmarksViewModelImpl(model,savedStateHandle)

        // Then view model selected sorting state should be set as saved value
        val selectedSortingState = WhiteBox.getInternalState(viewModel,"selectedSorting") as BehaviorSubject<BookmarkSorting>
        assertThat(selectedSortingState.value).isEqualTo(BookmarkSorting.OLDEST)
    }

    @Test
    fun fetchBookmarksFromModelWhenCreated() {
        // Given an initialized vew model

        // Then view model should request bookmarks from model
        val expectedBookmarkedRequest = BookmarksModelRequest(BookmarkSorting.NEWEST)
        verify { model.execute(expectedBookmarkedRequest) }
    }

    @Test
    fun updateViewBookmarksAndSortingStateWhenModelBookmarksUpdated() {
        // Given an initialized vew model

        // When model updates bookmarks state
        val modelBookmarks = createNewsHeadlines()
        modelBookmarksSubject.onNext(Result.Success(modelBookmarks))

        // Then view model should update view bookmarks state
        assertThat(viewModel.bookmarks.value).isInstanceOf(ViewData.Data::class.java)
        assertThat(viewModel.bookmarks.value!!.data).isEqualTo(modelBookmarks)

        // And update view sorting state to the selected sorting value
        val selectedSorting =
            WhiteBox.getInternalState(viewModel,"selectedSorting") as BehaviorSubject<BookmarkSorting>
        assertThat(viewModel.sorting.value).isEqualTo(selectedSorting.value)
    }

    @Test
    fun fetchBookmarksFromModelAccordingToSortingWhenAskedToSort() {
        // Given an initialized view model

        // When view ask view model to sort bookmarks
        val sorting = BookmarkSorting.OLDEST
        viewModel.sortBookmarks(sorting)

        // Then view model should pass sorting value to selected sorting publisher
        val selectedSorting =
            WhiteBox.getInternalState(viewModel,"selectedSorting") as BehaviorSubject<BookmarkSorting>

        assertThat(selectedSorting.value).isEqualTo(sorting)
    }

    @Test
    fun saveSelectedSortingValueWhenAskedToSort() {
        // Given an initialized view model

        // When view ask view model to sort bookmarks
        viewModel.sortBookmarks(BookmarkSorting.OLDEST)

        // Then view model should save sorting value in saved state
        assertThat(
            savedStateHandle.get<BookmarkSorting>("sorting")
        ).isEqualTo(BookmarkSorting.OLDEST)
    }
}