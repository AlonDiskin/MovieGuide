package com.diskin.alon.movieguide.reviews.presentation.controller

import android.content.Context
import android.os.Looper
import android.widget.RelativeLayout
import androidx.appcompat.view.menu.ActionMenuItem
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelLazy
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.paging.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.diskin.alon.movieguide.common.uitesting.HiltTestActivity
import com.diskin.alon.movieguide.common.uitesting.RecyclerViewMatcher.withRecyclerView
import com.diskin.alon.movieguide.common.uitesting.launchFragmentInHiltContainer
import com.diskin.alon.movieguide.common.uitesting.swipeToRefresh
import com.diskin.alon.movieguide.reviews.appservices.data.MovieSorting
import com.diskin.alon.movieguide.reviews.presentation.R
import com.diskin.alon.movieguide.reviews.presentation.controller.MoviesAdapter.MovieViewHolder
import com.diskin.alon.movieguide.reviews.presentation.createMovies
import com.diskin.alon.movieguide.reviews.presentation.data.Movie
import com.diskin.alon.movieguide.reviews.presentation.viewmodel.MoviesViewModel
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.verify
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import java.util.concurrent.CopyOnWriteArrayList

/**
 * [MoviesFragment] hermetic ui test class.
 */
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@SmallTest
@Config(sdk = [28])
class MoviesFragmentTest {

    // Test subject
    private lateinit var scenario: ActivityScenario<HiltTestActivity>

    // Collaborators
    private val viewModel: MoviesViewModel = mockk()

    // Stub data
    private val movies = MutableLiveData<PagingData<Movie>>()
    private val sorting = MutableLiveData<MovieSorting>()

    // Test nav controller
    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    @Before
    fun setUp() {
        // Stub view model creation with test mock
        mockkConstructor(ViewModelLazy::class)
        every { anyConstructed<ViewModelLazy<MoviesViewModel>>().value } returns viewModel

        // Stub collaborators
        every { viewModel.sorting } returns sorting
        every { viewModel.sortMovies(any()) } returns Unit
        every { viewModel.movies } returns movies

        // Setup test nav controller
        navController.setGraph(R.navigation.reviews_nav_graph)

        // Launch fragment under test
        scenario = launchFragmentInHiltContainer<MoviesFragment>()

        // Set the NavController property on the fragment with test controller
        scenario.onActivity {
            Navigation.setViewNavController(
                it.supportFragmentManager.fragments[0].requireView(),
                navController)
        }
    }

    @Test
    fun showMovies() {
        // Given a resumed fragment

        // When view model updates movies paging
        val movies = createMovies()
        this.movies.value = PagingData.from(movies)

        // Then fragment should display paging
        scenario.onActivity { activity ->
            val adapter = activity.findViewById<RecyclerView>(R.id.movies).adapter!!
            assertThat(adapter.itemCount).isEqualTo(movies.size)
        }

        movies.forEachIndexed { index, movie ->
            // Scroll to expected movie layout position
            onView(withId(R.id.movies))
                .perform(scrollToPosition<MovieViewHolder>(index))

            Shadows.shadowOf(Looper.getMainLooper()).idle()

            // Check list item at scrolled position display expected data
            onView(withRecyclerView(R.id.movies).atPosition(index))
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

            onView(withRecyclerView(R.id.movies).atPosition(index))
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
    fun showRefreshBarWhenMoviesPagingRefreshing() {
        // Given a resumed fragment

        // When movies paging data is refreshing
        scenario.onActivity { activity ->
            val adapter = activity.findViewById<RecyclerView>(R.id.movies).adapter!!
            val swipeRefreshLayout = activity.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
            val listener =
                getMoviesAdapterLoadStatesListener(adapter as MoviesAdapter)

            listener.invoke(
                CombinedLoadStates(
                    LoadState.Loading,
                    LoadState.NotLoading(true),
                    LoadState.NotLoading(true),
                    LoadStates(
                        LoadState.Loading,
                        LoadState.NotLoading(true),
                        LoadState.NotLoading(true)
                    )
                )
            )

            // Then fragment should show refresh bar
            assertThat(swipeRefreshLayout.isRefreshing).isTrue()
        }
    }

    @Test
    fun hideRefreshBarWhenMoviesPagingRefreshed() {
        // Given a resumed fragment

        // When movies paging data refreshing is finished
        scenario.onActivity { activity ->
            val adapter = activity.findViewById<RecyclerView>(R.id.movies).adapter!!
            val swipeRefreshLayout = activity.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
            val listener =
                getMoviesAdapterLoadStatesListener(adapter as MoviesAdapter)

            listener.invoke(
                CombinedLoadStates(
                    LoadState.NotLoading(false),
                    LoadState.NotLoading(true),
                    LoadState.NotLoading(true),
                    LoadStates(
                        LoadState.NotLoading(false),
                        LoadState.NotLoading(true),
                        LoadState.NotLoading(true)
                    )
                )
            )

            // Then fragment should hide refresh bar
            assertThat(swipeRefreshLayout.isRefreshing).isFalse()
        }
    }

    @Test
    fun refreshMoviesPagingWhenUserSwipeToRefresh() {
        // Given a resume fragment

        // When user performs swipe to refresh
        onView(withId(R.id.swipe_refresh))
            .perform(swipeToRefresh())

        // Then movies adapter should refresh paging
        scenario.onActivity {  activity ->
            val swipeRefreshLayout = activity.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
            assertThat(swipeRefreshLayout.isRefreshing).isTrue()
        }
    }

    @Test
    fun showProgressBarWhenMoviesPagingLoadAppendedPage() {
        // Given a resumed fragment

        // When movies paging loading an appended page
        scenario.onActivity { activity ->
            val adapter = activity.findViewById<RecyclerView>(R.id.movies).adapter!!
            val listener =
                getMoviesAdapterLoadStatesListener(adapter as MoviesAdapter)

            listener.invoke(
                CombinedLoadStates(
                    LoadState.NotLoading(false),
                    LoadState.NotLoading(true),
                    LoadState.Loading,
                    LoadStates(
                        LoadState.NotLoading(false),
                        LoadState.NotLoading(true),
                        LoadState.Loading
                    )
                )
            )
        }

        // Then fragment should show loading progress bar
        onView(withId(R.id.progress_bar))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun hideProgressBarWhenMoviesPagingAppendedPageLoaded() {
        // Given a resumed fragment

        // When movies paging appended page loading is finished
        scenario.onActivity { activity ->
            val adapter = activity.findViewById<RecyclerView>(R.id.movies).adapter!!
            val listener =
                getMoviesAdapterLoadStatesListener(adapter as MoviesAdapter)

            listener.invoke(
                CombinedLoadStates(
                    LoadState.NotLoading(false),
                    LoadState.NotLoading(true),
                    LoadState.NotLoading(true),
                    LoadStates(
                        LoadState.NotLoading(false),
                        LoadState.NotLoading(true),
                        LoadState.NotLoading(true)
                    )
                )
            )

            // Then fragment should hide loading progress bar
            onView(withId(R.id.progress_bar))
                .check(matches(withEffectiveVisibility(Visibility.GONE)))
        }
    }

    @Test
    fun showMoviesSorting() {
        // Given a resumed  fragment
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // When user open sorting menu
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        onView(withContentDescription(R.string.title_action_sort))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        listOf(
            MovieSorting.POPULARITY,
            MovieSorting.RATING,
            MovieSorting.RELEASE_DATE,
            MovieSorting.FAVORITE
        )
            .forEach {
                // And view model update movies sorting state
                sorting.value = it

                // Then fragment should show checked sorting type accordingly in menu
                when(it) {
                    MovieSorting.RELEASE_DATE -> {
                        onView(allOf(
                            hasDescendant(withText(R.string.title_action_sort_date)),
                            instanceOf(RelativeLayout::class.java)
                        ))
                            .check(matches(
                                hasSibling(isChecked())
                            ))
                    }

                    MovieSorting.RATING -> {
                        onView(allOf(
                            hasDescendant(withText(R.string.title_action_sort_rating)),
                            instanceOf(RelativeLayout::class.java)
                        ))
                            .check(matches(
                                hasSibling(isChecked())
                            ))
                    }

                    MovieSorting.POPULARITY -> {
                        onView(allOf(
                            hasDescendant(withText(R.string.title_action_sort_popular)),
                            instanceOf(RelativeLayout::class.java)
                        ))
                            .check(matches(
                                hasSibling(isChecked())
                            ))
                    }

                    MovieSorting.FAVORITE -> {
                        onView(allOf(
                            hasDescendant(withText(R.string.title_action_sort_favorite)),
                            instanceOf(RelativeLayout::class.java)
                        ))
                            .check(matches(
                                hasSibling(isChecked())
                            ))
                    }
                }
            }
    }

    @Test
    fun sortMoviesWhenSortingSelected() {
        // Given a resumed fragment

        // When user select a sorting type from sorting menu
        listOf(
            MovieSorting.POPULARITY,
            MovieSorting.RATING,
            MovieSorting.RELEASE_DATE
        ).forEach {
            val context = ApplicationProvider.getApplicationContext<Context>()
            val addMenuItem = when(it) {
                MovieSorting.RELEASE_DATE -> ActionMenuItem(
                    context,
                    0,
                    R.id.action_sort_date,
                    0,
                    0,
                    null
                )

                MovieSorting.POPULARITY -> ActionMenuItem(
                    context,
                    0,
                    R.id.action_sort_popular,
                    0,
                    0,
                    null
                )

                MovieSorting.RATING -> ActionMenuItem(
                    context,
                    0,
                    R.id.action_sort_rating,
                    0,
                    0,
                    null
                )

                MovieSorting.FAVORITE -> ActionMenuItem(
                    context,
                    0,
                    R.id.action_sort_favorite,
                    0,
                    0,
                    null
                )
            }

            scenario.onActivity { activity ->
                activity.supportFragmentManager.fragments[0].onOptionsItemSelected(addMenuItem)
            }

            // Then fragment should ask view model to sort movies according to user selection
            verify { viewModel.sortMovies(it) }
        }
    }

    @Test
    fun notifyUserOfErrorWithRetryWhenPagingRefreshOrAppendFailWithCause() {
        // Given a resume fragment

        // When paging refresh fail with error that contain error message
        val error = Throwable("error_message")
        scenario.onActivity { activity ->
            val adapter = activity.findViewById<RecyclerView>(R.id.movies).adapter!!
            val listener =
                getMoviesAdapterLoadStatesListener(adapter as MoviesAdapter)

            listener.invoke(
                CombinedLoadStates(
                    LoadState.Error(error),
                    LoadState.NotLoading(true),
                    LoadState.NotLoading(true),
                    LoadStates(
                        LoadState.Error(error),
                        LoadState.NotLoading(true),
                        LoadState.NotLoading(true)
                    )
                )
            )
        }

        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should display error message in snackbar
        onView(withId(R.id.snackbar_text))
            .check(matches(allOf(
                withText(error.message),
                isDisplayed()
            )))

        // And provide retry
        onView(withId(R.id.snackbar_action))
            .check(matches(
                allOf(
                    withText(R.string.action_retry),
                    isDisplayed()
                )
            ))

        // When paging append fail with error that contain error message
        scenario.onActivity { activity ->
            val adapter = activity.findViewById<RecyclerView>(R.id.movies).adapter!!
            val listener =
                getMoviesAdapterLoadStatesListener(adapter as MoviesAdapter)

            listener.invoke(
                CombinedLoadStates(
                    LoadState.NotLoading(false),
                    LoadState.NotLoading(true),
                    LoadState.Error(error),
                    LoadStates(
                        LoadState.NotLoading(false),
                        LoadState.NotLoading(true),
                        LoadState.Error(error)
                    )
                )
            )
        }

        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should display error message in snackbar
        onView(withId(R.id.snackbar_text))
            .check(matches(allOf(
                withText(error.message),
                isDisplayed()
            )))

        // And provide retry
        onView(withId(R.id.snackbar_action))
            .check(matches(
                allOf(
                    withText(R.string.action_retry),
                    isDisplayed()
                )
            ))
    }

    @Test
    fun notifyUserOfErrorWithoutRetryWhenPagingRefreshOrAppendFailWithNoCause() {
        // Given a resume fragment

        // When paging refresh fail without error message in error instance
        scenario.onActivity { activity ->
            val adapter = activity.findViewById<RecyclerView>(R.id.movies).adapter!!
            val listener =
                getMoviesAdapterLoadStatesListener(adapter as MoviesAdapter)

            listener.invoke(
                CombinedLoadStates(
                    LoadState.Error(Throwable()),
                    LoadState.NotLoading(true),
                    LoadState.NotLoading(true),
                    LoadStates(
                        LoadState.Error(Throwable()),
                        LoadState.NotLoading(true),
                        LoadState.NotLoading(true)
                    )
                )
            )
        }

        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should display generic error message in snackbar
        onView(withId(R.id.snackbar_text))
            .check(matches(allOf(
                withText(R.string.unknown_error),
                isDisplayed()
            )))

        // And do not provide a retry action
        onView(withId(R.id.snackbar_action))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))

        // When paging append fail without error message in error instance
        scenario.onActivity { activity ->
            val adapter = activity.findViewById<RecyclerView>(R.id.movies).adapter!!
            val listener =
                getMoviesAdapterLoadStatesListener(adapter as MoviesAdapter)

            listener.invoke(
                CombinedLoadStates(
                    LoadState.NotLoading(false),
                    LoadState.NotLoading(true),
                    LoadState.Error(Throwable()),
                    LoadStates(
                        LoadState.NotLoading(false),
                        LoadState.NotLoading(true),
                        LoadState.Error(Throwable())
                    )
                )
            )
        }

        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should display generic error message in snackbar
        onView(withId(R.id.snackbar_text))
            .check(matches(allOf(
                withText(R.string.unknown_error),
                isDisplayed()
            )))

        // And do not provide a retry action
        onView(withId(R.id.snackbar_action))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun hideErrorNotificationWhenPagingLoading() {
        // Given a resume fragment

        // When paging refreshing fail
        scenario.onActivity { activity ->
            val adapter = activity.findViewById<RecyclerView>(R.id.movies).adapter!!
            val listener =
                getMoviesAdapterLoadStatesListener(adapter as MoviesAdapter)

            listener.invoke(
                CombinedLoadStates(
                    LoadState.Error(Throwable()),
                    LoadState.NotLoading(true),
                    LoadState.NotLoading(true),
                    LoadStates(
                        LoadState.Error(Throwable()),
                        LoadState.NotLoading(true),
                        LoadState.NotLoading(true)
                    )
                )
            )
        }

        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // And paging refreshing again
        scenario.onActivity { activity ->
            val adapter = activity.findViewById<RecyclerView>(R.id.movies).adapter!!
            val listener =
                getMoviesAdapterLoadStatesListener(adapter as MoviesAdapter)

            listener.invoke(
                CombinedLoadStates(
                    LoadState.Loading,
                    LoadState.NotLoading(true),
                    LoadState.NotLoading(true),
                    LoadStates(
                        LoadState.Loading,
                        LoadState.NotLoading(true),
                        LoadState.NotLoading(true)
                    )
                )
            )
        }

        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should not display error notification
        onView(withId(R.id.snackbar_text))
            .check(doesNotExist())
    }

    @Test
    @Config(qualifiers = "port")
    fun showPortUiWhenInHandsetPortOrientation() {
        // Given a resumed fragment in port orientation shown in
        // handset device

        // Then fragment should display a handset port layout
        scenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.movies)
            val manager = recyclerView.layoutManager as GridLayoutManager
            val expectedSpan = activity.resources.getInteger(R.integer.movies_port_span)

            assertThat(manager.spanCount).isEqualTo(expectedSpan)
        }
    }

    @Test
    @Config(qualifiers = "land")
    fun showLandUiWhenInHandsetLandOrientation() {
        // Given a resumed fragment in land orientation shown in
        // handset device

        // Then fragment should display a handset land layout
        scenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.movies)
            val manager = recyclerView.layoutManager as GridLayoutManager
            val expectedSpan = activity.resources.getInteger(R.integer.movies_land_span)

            assertThat(manager.spanCount).isEqualTo(expectedSpan)
        }
    }

    @Test
    @Config(qualifiers = "sw600dp")
    fun showPortUiWhenInSmallTabletPortOrientation() {
        // Given a resumed fragment in port orientation shown in small tablet device

        // Then fragment should display a small tablet port layout
        scenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.movies)
            val manager = recyclerView.layoutManager as GridLayoutManager
            val expectedSpan = activity.resources.getInteger(R.integer.movies_small_tablet_port_span)

            assertThat(manager.spanCount).isEqualTo(expectedSpan)
        }
    }

    @Test
    @Config(qualifiers = "sw600dp-land")
    fun showLandUiWhenInSmallTabletLandOrientation() {
        // Given a resumed fragment in land orientation shown in small tablet device

        // Then fragment should display a small tablet land layout
        scenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.movies)
            val manager = recyclerView.layoutManager as GridLayoutManager
            val expectedSpan = activity.resources.getInteger(R.integer.movies_small_tablet_land_span)

            assertThat(manager.spanCount).isEqualTo(expectedSpan)
        }
    }

    @Test
    @Config(qualifiers = "sw720dp")
    fun showPortUiWhenInLargeTabletPortOrientation() {
        // Given a resumed fragment in port orientation shown in tablet device

        // Then fragment should display a large tablet port layout
        scenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.movies)
            val manager = recyclerView.layoutManager as GridLayoutManager
            val expectedSpan = activity.resources.getInteger(R.integer.movies_large_tablet_port_span)

            assertThat(manager.spanCount).isEqualTo(expectedSpan)
        }
    }

    @Test
    @Config(qualifiers = "sw720dp-land")
    fun showLandUiWhenInLargeTabletLandOrientation() {
        // Given a resumed fragment in land orientation shown in large tablet device

        // Then fragment should display a large tablet land layout
        scenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.movies)
            val manager = recyclerView.layoutManager as GridLayoutManager
            val expectedSpan = activity.resources.getInteger(R.integer.movies_large_tablet_land_span)

            assertThat(manager.spanCount).isEqualTo(expectedSpan)
        }
    }

    @Test
    fun openMovieReviewScreenWhenMovieSelected() {
        // Given an initialized fragment with displayed movies
        val movies = createMovies()
        this.movies.value = PagingData.from(movies)

        // When user click on first shown movie from list
        onView(withId(R.id.movies))
            .perform(actionOnItemAtPosition<MovieViewHolder>(0, click()))
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should open movie review screen via navigation controller
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.movieReviewFragment)

        // And pass movie id to destination
        val context = ApplicationProvider.getApplicationContext<Context>()
        assertThat(navController.currentBackStackEntry?.arguments?.get(context
            .getString(R.string.movie_id_arg)))
            .isEqualTo(movies.first().id)
    }

    @Test
    fun openMoviesSearchScreenWhenSearchSelected() {
        // Given a resumed fragment

        // When
        val context = ApplicationProvider.getApplicationContext<Context>()
        val menuItem = ActionMenuItem(
                context,
                0,
                R.id.action_search,
                0,
                0,
                null
            )

        scenario.onActivity { activity ->
            activity.supportFragmentManager.fragments[0].onOptionsItemSelected(menuItem)
        }

        // Then
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.moviesSearchFragment)
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