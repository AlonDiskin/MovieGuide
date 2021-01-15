package com.diskin.alon.movieguide.news.presentation.controller

import android.content.Context
import android.content.Intent
import android.os.Looper
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.menu.ActionMenuItem
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelLazy
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.diskin.alon.movieguide.common.presentation.ErrorViewData
import com.diskin.alon.movieguide.common.presentation.ImageLoader
import com.diskin.alon.movieguide.common.presentation.UpdateViewData
import com.diskin.alon.movieguide.common.uitesting.HiltTestActivity
import com.diskin.alon.movieguide.common.uitesting.RecyclerViewMatcher.withRecyclerView
import com.diskin.alon.movieguide.common.uitesting.launchFragmentInHiltContainer
import com.diskin.alon.movieguide.news.appservices.data.BookmarkSorting
import com.diskin.alon.movieguide.news.presentation.R
import com.diskin.alon.movieguide.news.presentation.controller.BookmarksAdapter.BookmarkViewHolder
import com.diskin.alon.movieguide.news.presentation.createNewsHeadlines
import com.diskin.alon.movieguide.news.presentation.data.Headline
import com.diskin.alon.movieguide.news.presentation.viewmodel.BookmarksViewModel
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowAlertDialog

/**
 * [BookmarksFragment] hermetic ui test.
 */
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@SmallTest
@Config(sdk = [28])
class BookmarksFragmentTest {

    // Test subject
    private lateinit var scenario: ActivityScenario<HiltTestActivity>

    // Collaborators
    private val viewModel: BookmarksViewModel = mockk()

    // Stub data
    private val bookmarks = MutableLiveData<List<Headline>>()
    private val sorting = MutableLiveData<BookmarkSorting>()
    private val error = MutableLiveData<ErrorViewData>()
    private val update = MutableLiveData<UpdateViewData>()

    // Test nav controller
    private val navController = TestNavHostController(getApplicationContext())

    @Before
    fun setUp() {
        // Stub view model creation with test mock
        mockkConstructor(ViewModelLazy::class)
        every { anyConstructed<ViewModelLazy<BookmarksViewModel>>().value } returns viewModel

        // Stub collaborators
        every { viewModel.bookmarks } returns bookmarks
        every { viewModel.sorting } returns sorting
        every { viewModel.sortBookmarks(any()) } returns Unit
        every { viewModel.update } returns update
        every { viewModel.error } returns error

        // Setup test nav controller
        navController.setGraph(R.navigation.bookmarks_nav_graph)

        // Launch fragment under test
        scenario = launchFragmentInHiltContainer<BookmarksFragment>()

        // Set the NavController property on the fragment with test controller
        scenario.onActivity {
            Navigation.setViewNavController(
                it.supportFragmentManager.fragments[0].requireView(),
                navController)
        }
    }

    @Test
    fun showAllUserBookmarks() {
        // Test case fixture
        mockkObject(ImageLoader)

        // Given a resume fragment

        // When bookmarks are available in view model
        val bookmarkedHeadlines = createNewsHeadlines()
        bookmarks.value = bookmarkedHeadlines

        // Then fragment should show bookmarks in layout
        bookmarkedHeadlines.forEachIndexed { index, newsHeadline ->
            onView(withId(R.id.bookmarked_articles))
                .perform(
                    scrollToPosition<BookmarkViewHolder>(
                        index
                    )
                )

            Shadows.shadowOf(Looper.getMainLooper()).idle()

            onView(withRecyclerView(R.id.bookmarked_articles).atPosition(index))
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
        bookmarks.value = bookmarkedHeadlines
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // When user selects the first listed bookmarks
        onView(withId(R.id.bookmarked_articles))
            .perform(
                actionOnItemAtPosition<BookmarkViewHolder>(
                    0,
                    click()
                )
            )
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should navigate user to article detail screen,passing it selected article id
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.articleActivity)
        val context = getApplicationContext<Context>()
        assertThat(
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

        onView(withContentDescription(R.string.title_action_sort))
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
        onView(withContentDescription(R.string.title_action_sort))
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

            scenario.onActivity {
                it.supportFragmentManager.fragments[0].onOptionsItemSelected(menuItem)
            }

            // Then fragment should ask view model to sort bookmarks by selected sorting
            verify { viewModel.sortBookmarks(it) }
        }
    }

    @Test
    fun showProgressBarWhenBookmarksViewsStateUpdated() {
        // Given a resumed fragment

        // When bookmarks are being updated
        update.value = UpdateViewData.Update

        // Then fragment should show progress indicator in ui
        onView(withId(R.id.progress_bar))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun hideProgressBarWhenBookmarksViewsStateNotUpdated() {
        // Given

        // When bookmarks update completes
        update.value = UpdateViewData.EndUpdate

        // Then fragment should hide progress indicator in ui
        onView(withId(R.id.progress_bar))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun shareBookmarkWhenUserSelectToShareIt() {
        // Test case fixture
        Intents.init()

        // Given an a resumed fragment with displayed bookmarks
        val bookmarkedHeadlines = createNewsHeadlines()
        bookmarks.value = listOf(bookmarkedHeadlines.first())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // When user select to share first shown bookmarked article
        onView(withId(R.id.bookmarkOptions))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        onView(withText(R.string.title_action_share_bookmark))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should display android share sheet for article url sharing
        Intents.intended(IntentMatchers.hasAction(Intent.ACTION_CHOOSER))
        Intents.intended(IntentMatchers.hasExtraWithKey(Intent.EXTRA_INTENT))

        val intent = Intents.getIntents().first().extras?.get(Intent.EXTRA_INTENT) as Intent
        val context = getApplicationContext<Context>()!!

        assertThat(intent.type).isEqualTo(context.getString(R.string.mime_type_text))
        assertThat(intent.getStringExtra(Intent.EXTRA_TEXT))
            .isEqualTo(bookmarkedHeadlines.first().articleUrl)

        Intents.release()
    }

    @Test
    fun removeBookmarkWhenUserSelectToRemoveIt() {
        // Test case fixture
        every { viewModel.removeBookmarks(any()) } returns Unit

        // Given an a resumed fragment with displayed bookmarks
        val bookmarkedHeadlines = createNewsHeadlines()
        bookmarks.value = listOf(bookmarkedHeadlines.first())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // When user select to remove first shown bookmark
        onView(withId(R.id.bookmarkOptions))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        onView(withText(R.string.title_action_remove_bookmark))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should show an alert dialog to confirm removal
         val dialog = (ShadowAlertDialog.getLatestDialog() as AlertDialog)
        assertThat(dialog.isShowing).isTrue()

        // When user approve confirm removal
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick()
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should ask view model to remove bookmark
        verify { viewModel.removeBookmarks(listOf(bookmarkedHeadlines.first().id)) }
    }

    @Test
    fun enableMultipleBookmarksRemoval() {
        // Test case fixture
        every { viewModel.removeBookmarks(any()) } returns Unit

        // Given an a resumed fragment with displayed bookmarks
        val bookmarkedHeadlines = createNewsHeadlines()
        bookmarks.value = bookmarkedHeadlines
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // When user selects to remove first and last shown bookmarks
        onView(withId(R.id.bookmarked_articles))
            .perform(scrollToPosition<BookmarkViewHolder>(0))
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        onView(withRecyclerView(R.id.bookmarked_articles).atPosition(0))
            .perform(longClick())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        onView(withId(R.id.bookmarked_articles))
            .perform(scrollToPosition<BookmarkViewHolder>(bookmarkedHeadlines.size - 1))
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        onView(withRecyclerView(R.id.bookmarked_articles).atPosition(bookmarkedHeadlines.size - 1))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        onView(withContentDescription(R.string.title_action_remove_bookmark))
            .perform(click())

        // Then fragment should show an alert dialog to confirm removal
        val dialog = (ShadowAlertDialog.getLatestDialog() as AlertDialog)
        assertThat(dialog.isShowing).isTrue()

        // When user approve confirm removal
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick()
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should ask view model to remove selected bookmarks
        verify { viewModel.removeBookmarks(
            listOf(
                bookmarkedHeadlines.first().id,
                bookmarkedHeadlines.last().id)
        )
        }
    }
}