package com.diskin.alon.movieguide.news.presentation.controller

import android.content.Context
import android.os.Looper
import android.widget.RelativeLayout
import androidx.appcompat.view.menu.ActionMenuItem
import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.diskin.alon.movieguide.common.presentation.ImageLoader
import com.diskin.alon.movieguide.common.presentation.ViewData
import com.diskin.alon.movieguide.common.presentation.ViewDataError
import com.diskin.alon.movieguide.common.uitesting.RecyclerViewMatcher.withRecyclerView
import com.diskin.alon.movieguide.news.appservices.data.BookmarkSorting
import com.diskin.alon.movieguide.news.presentation.R
import com.diskin.alon.movieguide.news.presentation.controller.BookmarksAdapter.BookmarkViewHolder
import com.diskin.alon.movieguide.news.presentation.createNewsHeadlines
import com.diskin.alon.movieguide.news.presentation.data.Headline
import com.diskin.alon.movieguide.news.presentation.viewmodel.BookmarksViewModel
import com.google.common.truth.Truth
import dagger.android.support.AndroidSupportInjection
import io.mockk.*
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

/**
 * [BookmarksFragment] hermetic ui test.
 */
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@SmallTest
@Config(sdk = [28])
class BookmarksFragmentTest {

    // Test subject
    private lateinit var scenario: FragmentScenario<BookmarksFragment>

    // Collaborators
    private val viewModel: BookmarksViewModel = mockk()

    // Stub data
    private val bookmarks = MutableLiveData<ViewData<List<Headline>>>()
    private val sorting = MutableLiveData<BookmarkSorting>()

    // Test nav controller
    private val navController = TestNavHostController(getApplicationContext())

    @Before
    fun setUp() {
        // Mock out dagger di
        val fragmentSlot = slot<BookmarksFragment>()
        mockkStatic(AndroidSupportInjection::class)
        every { AndroidSupportInjection.inject(capture(fragmentSlot)) } answers {
            fragmentSlot.captured.viewModel = viewModel
        }

        // Stub collaborators
        every { viewModel.bookmarks } returns bookmarks
        every { viewModel.sorting } returns sorting
        every { viewModel.sortBookmarks(any()) } returns Unit

        // Setup test nav controller
        navController.setGraph(R.navigation.bookmarks_nav_graph)

        // Launch fragment under test
        scenario = FragmentScenario.launchInContainer(
            BookmarksFragment::class.java,
            null,
            R.style.AppTheme,
            null
        )

        // Set the NavController property on the fragment with test controller
        scenario.onFragment {
            Navigation.setViewNavController(it.requireView(), navController)
        }
    }

    @Test
    fun showAllUserBookmarks() {
        // Test case fixture
        mockkObject(ImageLoader)

        // Given a resume fragment

        // When bookmarks are available in view model
        val bookmarkedHeadlines = createNewsHeadlines()
        bookmarks.value = ViewData.Data(bookmarkedHeadlines)

        // Then fragment should show bookmarks in layout
        bookmarkedHeadlines.forEachIndexed { index, newsHeadline ->
            onView(withId(R.id.bookmarks))
                .perform(
                    scrollToPosition<BookmarkViewHolder>(
                        index
                    )
                )

            Shadows.shadowOf(Looper.getMainLooper()).idle()

            onView(withRecyclerView(R.id.bookmarks).atPosition(index))
                .check(
                    matches(
                        hasDescendant(
                            allOf(
                                withId(R.id.published),
                                withText(newsHeadline.date)
                            )
                        )
                    )
                )
                .check(
                    matches(
                        hasDescendant(
                            allOf(
                                withId(R.id.title),
                                withText(newsHeadline.title),
                                isDisplayed()
                            )
                        )
                    )
                )

            verify { ImageLoader.loadIntoImageView(any(),newsHeadline.imageUrl) }
        }
    }

    @Test
    fun openBookmarkedArticleScreenWhenBookmarkViewSelected() {
        // Given a resumed fragment with shown bookmarks
        val bookmarkedHeadlines = createNewsHeadlines()
        bookmarks.value = ViewData.Data(bookmarkedHeadlines)
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // When user selects the first listed bookmarks
        onView(withId(R.id.bookmarks))
            .perform(
                actionOnItemAtPosition<BookmarkViewHolder>(
                    0,
                    click()
                )
            )
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should navigate user to article detail screen,passing it selected article id
        Truth.assertThat(navController.currentDestination?.id).isEqualTo(R.id.articleActivity)
        val context = getApplicationContext<Context>()
        Truth.assertThat(
            navController.currentBackStackEntry?.arguments?.get(
                context.getString(R.string.key_article_id))
        )
            .isEqualTo(bookmarkedHeadlines.first().id)
    }

    @Test
    fun showBookmarksViewsSortingMenuWhenMenuOpened() {
        // Given a resumed fragment

        // When bookmarks sorting view model state set as 'newest'
        sorting.value = BookmarkSorting.NEWEST

        // And use open sorting menu in ui
        openActionBarOverflowOrOptionsMenu(getApplicationContext())
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        onView(withText(R.string.title_action_sort))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment ui sorting menu should show 'newest' sorting as checked
        onView(allOf(
            hasDescendant(withText(R.string.title_action_sort_newest)),
            instanceOf(RelativeLayout::class.java)
        ))
            .check(matches(
                hasSibling(isChecked())
            ))

        // When bookmarks sorting view model state set as 'oldest'
        sorting.value = BookmarkSorting.OLDEST

        // And use open sorting menu in ui
        openActionBarOverflowOrOptionsMenu(getApplicationContext())
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        onView(withText(R.string.title_action_sort))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment ui sorting menu should show 'oldest' sorting as checked
        onView(allOf(
            hasDescendant(withText(R.string.title_action_sort_oldest)),
            instanceOf(RelativeLayout::class.java)
        ))
            .check(matches(
                hasSibling(isChecked())
            ))
    }

    @Test
    fun sortBookmarksViewsWhenUserSelectToSort() {
        val context = getApplicationContext<Context>()

        // Given a resumed fragment

        BookmarkSorting.values().toList().forEach {
            // When user select a bookmarks sorting type from menu
            val menuItem = when(it) {
                BookmarkSorting.NEWEST -> ActionMenuItem(
                    context,
                    0,
                    R.id.action_sort_newest,
                    0,
                    0,
                    null
                )

                BookmarkSorting.OLDEST -> ActionMenuItem(
                    context,
                    0,
                    R.id.action_sort_oldest,
                    0,
                    0,
                    null
                )
            }

            scenario.onFragment { fragment ->
                fragment.onOptionsItemSelected(menuItem)
            }

            // Then fragment should ask view model to sort bookmarks by selected sorting
            verify { viewModel.sortBookmarks(it) }
        }
    }

    @Test
    fun showProgressBarWhenBookmarksViewsStateUpdated() {
        // Given a resumed fragment

        // When bookmarks are being updated
        bookmarks.value = ViewData.Updating()

        // Then fragment should show progress indicator in ui
        onView(withId(R.id.progress_bar))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun hideProgressBarWhenBookmarksViewsStateNotUpdated() {
        // Given

        // When bookmarks are view state fail
        bookmarks.value = ViewData.Error(ViewDataError.NotRetriable("message"))

        // Then fragment should hide progress indicator in ui
        onView(withId(R.id.progress_bar))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))

        // When bookmarks update completes
        bookmarks.value = ViewData.Data(emptyList())

        // Then fragment should hide progress indicator in ui
        onView(withId(R.id.progress_bar))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
    }
}