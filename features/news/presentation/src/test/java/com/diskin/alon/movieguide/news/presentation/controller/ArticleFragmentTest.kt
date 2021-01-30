package com.diskin.alon.movieguide.news.presentation.controller

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelLazy
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.diskin.alon.movieguide.common.presentation.ErrorViewData
import com.diskin.alon.movieguide.common.presentation.UpdateViewData
import com.diskin.alon.movieguide.common.uitesting.HiltTestActivity
import com.diskin.alon.movieguide.common.uitesting.launchFragmentInHiltContainer
import com.diskin.alon.movieguide.news.presentation.R
import com.diskin.alon.movieguide.news.presentation.createBookmarkedTestArticle
import com.diskin.alon.movieguide.news.presentation.createTestArticle
import com.diskin.alon.movieguide.news.presentation.createUnBookmarkedTestArticle
import com.diskin.alon.movieguide.news.presentation.data.Article
import com.diskin.alon.movieguide.news.presentation.viewmodel.ArticleViewModel
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.verify
import org.hamcrest.CoreMatchers.allOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowToast

/**
 * [ArticleFragment] hermetic ui test class.
 */
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@SmallTest
@Config(sdk = [28])
class ArticleFragmentTest {

    // Test subject
    private lateinit var scenario: ActivityScenario<HiltTestActivity>

    // Collaborators
    private val viewModel = mockk<ArticleViewModel>()

    // Stub data
    private val articleViewData = MutableLiveData<Article>()
    private val errorViewData =  MutableLiveData<ErrorViewData>()
    private val updateViewData = MutableLiveData<UpdateViewData>()

    // Test nav controller
    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    @Before
    fun setUp() {
        // Stub view model creation with test mock
        mockkConstructor(ViewModelLazy::class)
        every { anyConstructed<ViewModelLazy<ArticleViewModel>>().value } returns viewModel

        // Stub mocked view model
        every { viewModel.article } returns articleViewData
        every { viewModel.update } returns updateViewData
        every { viewModel.error } returns errorViewData

        // Setup test nav controller
        navController.setGraph(R.navigation.news_nav_graph)
        navController.setCurrentDestination(R.id.articleFragment)

        // Launch fragment under test
        scenario = launchFragmentInHiltContainer<ArticleFragment>()

        // Set the NavController property on the fragment with test controller
        scenario.onActivity {
            Navigation.setViewNavController(
                it.supportFragmentManager.fragments[0].requireView(),
                navController)
        }
    }

    @Test
    fun showArticle() {
        // Given a resumed activity

        // When view model update article state
        val article = createTestArticle()
        articleViewData.value = article
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then activity should show article data in its layout
        onView(withId(R.id.title))
            .check(matches(withText(article.title)))

        onView(withId(R.id.published))
            .check(matches(withText(article.date)))

        onView(withId(R.id.author))
            .check(matches(withText(article.author)))

        onView(withId(R.id.content))
            .check(matches(withText(article.content)))

        onView(withId(R.id.action_bookmarking))
            .check(matches(withContentDescription(
                if (article.bookmarked)
                    R.string.title_action_unbookmark
                else
                    R.string.title_action_bookmark
            )))
    }

    @Test
    fun shareArticleWhenArticleSharingSelected() {
        // Test case fixture
        Intents.init()

        // Given a resumed activity with article state in view model
        val article = createTestArticle()
        articleViewData.value = article

        // And user clicks on the share button
        onView(withId(R.id.action_share))
            .perform(click())

        // Then activity should share article url via Android Sharesheet
        Intents.intended(IntentMatchers.hasAction(Intent.ACTION_CHOOSER))
        Intents.intended(IntentMatchers.hasExtraWithKey(Intent.EXTRA_INTENT))

        val intent = Intents.getIntents().first().extras?.get(Intent.EXTRA_INTENT) as Intent
        val context = ApplicationProvider.getApplicationContext<Context>()!!

        assertThat(intent.type).isEqualTo(context.getString(R.string.mime_type_text))
        assertThat(intent.getStringExtra(Intent.EXTRA_TEXT))
            .isEqualTo(article.articleUrl)

        Intents.release()
    }

    @Test
    fun notifySharingUnavailableWhenArticleNotLoaded() {
        // Given a resumed activity with article null state in view model

        // And user clicks on the share button
        onView(withId(R.id.action_share))
            .perform(click())

        // Then activity should notify user that sharing action is unavailable
        val toastMessage = ApplicationProvider.getApplicationContext<Context>()
            .getString(R.string.title_action_not_available)

        assertThat(ShadowToast.getTextOfLatestToast().toString()).isEqualTo(toastMessage)
    }

    @Test
    fun showProgressBarWhenViewDataUpdating() {
        // Given a resumed activity

        // When view data is updated
        updateViewData.value = UpdateViewData.Update
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then activity should show progress bar
        onView(withId(R.id.progress_bar))
            .check(matches(isDisplayed()))
    }

    @Test
    fun hideProgressBarWhenViewDataNotUpdating() {
        // Given a resumed activity

        // When view data is not updating
        updateViewData.value = UpdateViewData.EndUpdate
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then activity should hide progress bar
        onView(withId(R.id.progress_bar))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun showErrorNotificationViewDataUponError() {
        // Given a resumed activity

        // When view data fail with retriable error
        val error = ErrorViewData.Retriable("message"){}
        errorViewData.value = error
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then activity should display a snack bar with error message
        onView(withId(R.id.snackbar_text))
            .check(matches(
                allOf(
                    withText(error.reason),
                    isDisplayed()
                )
            ))

        // And provide a retry action for failed operation
        onView(withId(R.id.snackbar_action))
            .check(matches(
                allOf(
                    withText(R.string.action_retry),
                    isDisplayed()
                )
            ))
    }

    @Test
    fun hideErrorNotificationWhenViewDataHasNoError() {
        // Given a resumed activity

        // When article view data has no errors
        errorViewData.value = ErrorViewData.NoError
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then activity should hide the error notification
        onView(withId(R.id.snackbar_text))
            .check(doesNotExist())
    }

    @Test
    fun showEmptyAppBarTitle() {
        // Given a resumed fragment

        // Then fragment should show an empty title in appbar
        assertThat(navController.currentDestination?.label).isEqualTo("")
    }

    @Test
    @Config(qualifiers = "land")
    fun showLandUiWhenDeviceInLandOrientation() {
        // Given article activity is resumed in device set in land orientation

        // Then activity should display land layout
        onView(withId(R.id.article_root_land))
            .check(matches(isDisplayed()))
    }

    @Test
    fun bookmarkArticleWhenUserSelectForUnbookmarkedArticle() {
        // Test case fixture
        every { viewModel.bookmark() } returns Unit

        // Given a resumed activity with displayed unbookmarked article
        articleViewData.value = createUnBookmarkedTestArticle()
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // When user select to bookmark article
        onView(withId(R.id.action_bookmarking))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then activity should ask view model to bookmark article
        verify { viewModel.bookmark() }
    }

    @Test
    fun uBookmarkArticleWhenUserSelectForBookmarkedArticle() {
        // Test case fixture
        every { viewModel.unBookmark() } returns Unit

        // Given a resumed activity with displayed bookmarked article
        articleViewData.value = createBookmarkedTestArticle()
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // When user select to un bookmark article
        onView(withId(R.id.action_bookmarking))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then activity should ask view model to un bookmark article
        verify { viewModel.unBookmark() }
    }

    @Test
    fun openArticleWebOriginWhenSelected() {
        // Test case fixture
        Intents.init()

        // Given a resumed activity with displayed article
        val article = createTestArticle()
        articleViewData.value = article
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // When user select to read original article
        onView(withId(R.id.fab))
            .perform(click())

        // Then open original article via user selected app
        Intents.intended(IntentMatchers.hasAction(Intent.ACTION_CHOOSER))

        val intent = Intents.getIntents().first().extras?.get(Intent.EXTRA_INTENT) as Intent

        assertThat(intent.action).isEqualTo(Intent.ACTION_VIEW)
        assertThat(intent.data).isEqualTo(Uri.parse(article.articleUrl))

        Intents.release()
    }
}