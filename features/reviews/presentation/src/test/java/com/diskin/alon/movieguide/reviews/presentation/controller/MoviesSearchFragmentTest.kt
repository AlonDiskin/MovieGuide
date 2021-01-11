package com.diskin.alon.movieguide.reviews.presentation.controller

import android.os.Looper
import android.view.KeyEvent
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.paging.*
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.Visibility.GONE
import androidx.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.diskin.alon.movieguide.common.appservices.AppError
import com.diskin.alon.movieguide.common.uitesting.RecyclerViewMatcher.withRecyclerView
import com.diskin.alon.movieguide.reviews.presentation.R
import com.diskin.alon.movieguide.reviews.presentation.TestSingleFragmentActivity
import com.diskin.alon.movieguide.reviews.presentation.controller.MoviesAdapter.MovieViewHolder
import com.diskin.alon.movieguide.reviews.presentation.createMovies
import com.diskin.alon.movieguide.reviews.presentation.data.Movie
import com.diskin.alon.movieguide.reviews.presentation.viewmodel.MoviesSearchViewModel
import com.google.common.truth.Truth.assertThat
import dagger.android.support.AndroidSupportInjection
import io.mockk.*
import kotlinx.android.synthetic.main.fragment_search_movies.*
import org.hamcrest.CoreMatchers.allOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import java.util.concurrent.CopyOnWriteArrayList

/**
 * [MoviesSearchFragment] hermetic ui test class.
 */
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@SmallTest
@Config(sdk = [28])
class MoviesSearchFragmentTest {

    // Test subject
    private lateinit var scenario: ActivityScenario<TestSingleFragmentActivity>

    // Collaborators
    private val viewModel: MoviesSearchViewModel = mockk()

    // Stub data
    private val results = MutableLiveData<PagingData<Movie>>()
    private val searchText = "text"
    private val searchTextSlot = slot<String>()

    // Test nav controller
    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    @Before
    fun setUp() {
        // Mock out dagger
        val slot  = slot<MoviesSearchFragment>()
        mockkStatic(AndroidSupportInjection::class)
        every { AndroidSupportInjection.inject(capture(slot)) } answers {
            slot.captured.viewModel = viewModel
        }

        // Stub collaborators
        searchTextSlot.captured = ""
        every { viewModel.results } returns results
        every { viewModel.searchText = capture(searchTextSlot) } answers { }
        // dynamic answer and not fixed return value(!),for ui recreation testing
        every { viewModel.searchText } answers { searchTextSlot.captured }

        // Setup test nav controller
        navController.setGraph(R.navigation.reviews_nav_graph)

        // Launch fragment under test
        scenario = ActivityScenario.launch(TestSingleFragmentActivity::class.java)
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Set the NavController property on the fragment with test controller
        scenario.onActivity {
            val fragment = it.supportFragmentManager.fragments[0]
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
    }

    @Test
    fun expandHintedSearchViewWhenResumed() {
        // Given a resumed fragment

        // Then
        onView(withHint(R.string.search_hint))
            .check(matches(withEffectiveVisibility(VISIBLE)))
    }

    @Test
    fun searchMoviesWithTypedQueryUponUserSelection() {
        // Test case fixture
        every { viewModel.search(any()) } returns Unit

        // Given a resumed fragment

        // When user search for movies by typing and submitting a query
        val query = "query"
        onView(withHint(R.string.search_hint))
            .perform(typeText(query))
            .perform(pressImeActionButton())

        // Then fragment should delegate search to view model
        verify { viewModel.search(query) }
    }

    @Test
    fun showSearchResultWhenResultsAvailable() {
        // Given a resumed fragment

        // When view model search results are updated
        val movies = createMovies()
        results.value = PagingData.from(movies)
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should show them in ui layout
        scenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.search_results)
            val adapter = recyclerView.adapter!!

            assertThat(adapter.itemCount).isEqualTo(movies.size)
        }

        movies.forEachIndexed { index, movie ->
            // Scroll to expected movie layout position
            onView(withId(R.id.search_results))
                .perform(scrollToPosition<MovieViewHolder>(index))

            Shadows.shadowOf(Looper.getMainLooper()).idle()

            // Check list item at scrolled position display expected data
            onView(withRecyclerView(R.id.search_results).atPosition(index))
                .check(
                    matches(
                        hasDescendant(
                            allOf(
                                withId(R.id.title),
                                withText(movie.title)
                            )
                        )
                    )
                )

            onView(withRecyclerView(R.id.search_results).atPosition(index))
                .check(
                    matches(
                        hasDescendant(
                            allOf(
                                withId(R.id.rating),
                                withText(movie.rating)
                            )
                        )
                    )
                )
        }
    }

    @Test
    fun showProgressIndicatorWhenSearchPerformed() {
        // Given a resumed fragment

        // When paged data loading initial page of search results
        scenario.onActivity {
            val listener = getMoviesAdapterLoadStatesListener(
                it.search_results.adapter as MoviesAdapter
            )

            listener.invoke(
                CombinedLoadStates(
                    LoadStates(
                        LoadState.Loading,
                        LoadState.NotLoading(true),
                        LoadState.NotLoading(true)
                    )
                )
            )
        }

        Shadows.shadowOf(Looper.getMainLooper()).runToEndOfTasks()

        // Then fragment should show loading progress bar
        onView(withId(R.id.progress_bar))
            .check(matches(withEffectiveVisibility(VISIBLE)))

        // When paged data loading appended page of search results
        scenario.onActivity {
            val listener = getMoviesAdapterLoadStatesListener(
                it.search_results.adapter as MoviesAdapter
            )

            listener.invoke(
                CombinedLoadStates(
                    LoadStates(
                        LoadState.NotLoading(false),
                        LoadState.NotLoading(true),
                        LoadState.Loading
                    )
                )
            )
        }

        Shadows.shadowOf(Looper.getMainLooper()).runToEndOfTasks()

        // Then fragment should show loading progress bar
        onView(withId(R.id.progress_bar))
            .check(matches(withEffectiveVisibility(VISIBLE)))
    }

    @Test
    fun hideSearchProgressIndicatorWhenNotSearching() {
        // Given a resumed fragment

        // When paged data not loading search results
        scenario.onActivity {
            val listener = getMoviesAdapterLoadStatesListener(
                it.search_results.adapter as MoviesAdapter
            )

            listener.invoke(
                CombinedLoadStates(
                    LoadStates(
                        LoadState.NotLoading(false),
                        LoadState.NotLoading(true),
                        LoadState.NotLoading(true)
                    )
                )
            )
        }

        Shadows.shadowOf(Looper.getMainLooper()).runToEndOfTasks()

        // Then fragment should hide loading progress bar
        onView(withId(R.id.progress_bar))
            .check(matches(withEffectiveVisibility(GONE)))
    }

    @Test
    fun showErrorIndicatorWhenSearchFail() {
        // Given

        // When paged search results loading fail with retriable AppError error
        val error = AppError("error message",true)
        scenario.onActivity {
            val listener = getMoviesAdapterLoadStatesListener(
                it.search_results.adapter as MoviesAdapter
            )

            listener.invoke(
                CombinedLoadStates(
                    LoadStates(
                        LoadState.Error(error),
                        LoadState.NotLoading(true),
                        LoadState.NotLoading(true)
                    )
                )
            )
        }

        Shadows.shadowOf(Looper.getMainLooper()).runToEndOfTasks()

        // Then fragment should show a snackbar with error message with retry action
        onView(withId(R.id.snackbar_text))
            .check(matches(allOf(
                withText(error.description),
                isDisplayed()
            )))

        onView(withId(R.id.snackbar_action))
            .check(matches(isDisplayed()))

        // When paged loading fail with not retriable throwable error
        scenario.onActivity {
            val listener = getMoviesAdapterLoadStatesListener(
                it.search_results.adapter as MoviesAdapter
            )

            listener.invoke(
                CombinedLoadStates(
                    LoadStates(
                        LoadState.Error(Throwable()),
                        LoadState.NotLoading(true),
                        LoadState.NotLoading(true)
                    )
                )
            )
        }

        Shadows.shadowOf(Looper.getMainLooper()).runToEndOfTasks()

        // Then fragment should show a snackbar with error message without retry action
        onView(withId(R.id.snackbar_text))
            .check(matches(allOf(
                withText(R.string.unknown_error),
                isDisplayed()
            )))

        onView(withId(R.id.snackbar_action))
            .check(matches(withEffectiveVisibility(GONE)))
    }

    @Test
    fun hideErrorIndicatorWhenSearchPerformed() {
        // Given a resumed fragment

        scenario.onActivity {
            val listener = getMoviesAdapterLoadStatesListener(
                it.search_results.adapter as MoviesAdapter
            )

            // When paged data loading initial page of search results
            listener.invoke(
                CombinedLoadStates(
                    LoadStates(
                        LoadState.Loading,
                        LoadState.NotLoading(true),
                        LoadState.NotLoading(true)
                    )
                )
            )

            // And loading fail
            listener.invoke(
                CombinedLoadStates(
                    LoadStates(
                        LoadState.Error(Throwable()),
                        LoadState.NotLoading(true),
                        LoadState.NotLoading(true)
                    )
                )
            )

            // And later paged data loading initial page of search results again
            listener.invoke(
                CombinedLoadStates(
                    LoadStates(
                        LoadState.Loading,
                        LoadState.NotLoading(true),
                        LoadState.NotLoading(true)
                    )
                )
            )
        }

        Shadows.shadowOf(Looper.getMainLooper()).runToEndOfTasks()

        // Then fragment should hide error snackbar
        onView(withId(R.id.snackbar_text))
            .check(doesNotExist())
    }

    @Test
    fun hideErrorIndicatorWhenResultsAvailable() {
        // Given a resumed fragment

        scenario.onActivity {
            val listener = getMoviesAdapterLoadStatesListener(
                it.search_results.adapter as MoviesAdapter
            )

            // When paged data search result loading fail
            listener.invoke(
                CombinedLoadStates(
                    LoadStates(
                        LoadState.Error(Throwable()),
                        LoadState.NotLoading(true),
                        LoadState.NotLoading(true)
                    )
                )
            )

            // And later loading complete with success
            listener.invoke(
                CombinedLoadStates(
                    LoadStates(
                        LoadState.NotLoading(false),
                        LoadState.NotLoading(true),
                        LoadState.NotLoading(true)
                    )
                )
            )
        }

        Shadows.shadowOf(Looper.getMainLooper()).runToEndOfTasks()

        // Then fragment should hide error snackbar
        onView(withId(R.id.snackbar_text))
            .check(doesNotExist())
    }

    @Test
    fun showEmptySearchIndicatorWhenNoSearchResults() {
        // Given a resumed fragment

        // When search results updated with results
        results.value = PagingData.from(createMovies())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // And later search results updated with empty result
        results.value = PagingData.empty()
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should show a message in ui indicating empty results
        onView(withText(R.string.text_empty_search))
            .check(matches(isDisplayed()))
    }

    @Test
    fun hideEmptySearchIndicatorWhenSearchResultsNotEmpty() {
        // Given a resumed fragment

        // When search results updated with empty result
        results.value = PagingData.empty()
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // And later search results updated with results
        results.value = PagingData.from(createMovies())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should hide empty results message
        onView(withText(R.string.text_empty_search))
            .check(matches(withEffectiveVisibility(GONE)))
    }

    @Test
    fun restorePrevQueryTextStateWhenResumed() {
        // Given a resumed fragment

        // When user type a search query
        val query = "query"
        onView(withId(R.id.search_src_text))
            .perform(typeText(query))
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // And later fragment is recreated
        scenario.recreate()
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should restore prev state of search text query
        onView(withId(R.id.search_src_text))
            .check(matches(withText(query)))
    }

    @Test
    fun navBackToMoviesScreenWhenUserCloseSearchField() {
        // Given a resumed fragment

        // When user collapse search view
        pressBack()

        // Then fragment nav controller should nav user to movies fragment
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.moviesFragment)
    }

    private fun getMoviesAdapterLoadStatesListener(adapter: MoviesAdapter): (CombinedLoadStates) -> Unit {
        val field = PagingDataAdapter::class.java.getDeclaredField("differ")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val differ = field.get(adapter) as AsyncPagingDataDiffer<Movie>

        val field2 = AsyncPagingDataDiffer::class.java.getDeclaredField("differBase")
        field2.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val differBase = field2.get(differ) as PagingDataDiffer<Movie>

        val field3 = PagingDataDiffer::class.java.getDeclaredField("loadStateListeners")
        field3.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val listeners = field3.get(differBase) as CopyOnWriteArrayList<(CombinedLoadStates) -> Unit>

        return listeners[1]
    }
}