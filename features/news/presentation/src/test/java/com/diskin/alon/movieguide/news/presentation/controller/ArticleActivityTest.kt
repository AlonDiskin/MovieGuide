package com.diskin.alon.movieguide.news.presentation.controller

import android.content.Context
import android.content.Intent
import android.os.Looper
import androidx.lifecycle.MutableLiveData
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
import com.diskin.alon.movieguide.common.appservices.AppError
import com.diskin.alon.movieguide.common.presentation.LoadState
import com.diskin.alon.movieguide.news.presentation.R
import com.diskin.alon.movieguide.news.presentation.createTestArticle
import com.diskin.alon.movieguide.news.presentation.data.Article
import com.diskin.alon.movieguide.news.presentation.viewmodel.ArticleViewModel
import com.google.common.truth.Truth.assertThat
import dagger.android.AndroidInjection
import io.mockk.*
import kotlinx.android.synthetic.main.activity_article.*
import org.hamcrest.CoreMatchers.allOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowToast

/**
 * [ArticleActivity] unit test.
 */
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@SmallTest
@Config(sdk = [28])
class ArticleActivityTest {

    // Test subject
    private lateinit var scenario: ActivityScenario<ArticleActivity>

    // Collaborators
    private val viewModel = mockk<ArticleViewModel>()

    // Stub data
    private val article = MutableLiveData<Article>()
    private val loading = MutableLiveData<LoadState>()

    @Before
    fun setUp() {
        // Mock out dagger di
        mockkStatic(AndroidInjection::class)
        val slot  = slot<ArticleActivity>()
        every { AndroidInjection.inject(capture(slot)) } answers {
            slot.captured.viewModel = viewModel
        }

        // Stub mocked view model
        every { viewModel.article } returns article
        every { viewModel.loading } returns loading

        // Launch activity under test
        scenario = ActivityScenario.launch(ArticleActivity::class.java)
    }

    @Test
    fun showArticle() {
        // Given a resumed activity

        // When view model update article state
        val testArticle = createTestArticle()
        article.value = testArticle
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then activity should show article data in its layout
        onView(withId(R.id.title))
            .check(matches(withText(testArticle.title)))

        onView(withId(R.id.published))
            .check(matches(withText(testArticle.date)))

        onView(withId(R.id.author))
            .check(matches(withText(testArticle.author)))

        onView(withId(R.id.content))
            .check(matches(withText(testArticle.content)))
    }

    @Test
    fun shareArticleWhenArticleSharingSelected() {
        // Test case fixture
        Intents.init()

        // Given a resumed activity with article state in view model
        val testArticle = createTestArticle()
        article.value = testArticle

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
            .isEqualTo(testArticle.articleUrl)

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
    fun showProgressBarWhenArticleLoading() {
        // Given a resumed activity

        // When view model update loading state to 'loading'
        loading.value = LoadState.Loading
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then activity should show progress bar
        onView(withId(R.id.progress_bar))
            .check(matches(isDisplayed()))
    }

    @Test
    fun hideProgressBarWhenArticleSuccessfullyLoaded() {
        // Given a resumed activity

        // When view model update loading state to 'not loading'
        loading.value = LoadState.Success
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then activity should hide progress bar
        onView(withId(R.id.progress_bar))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun hideProgressBarWhenArticleLoadingFail() {
        // Given a resumed activity

        // When update loading state to 'not loading'
        loading.value = LoadState.Error(AppError("error",false))
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then activity should show progress bar
        onView(withId(R.id.progress_bar))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun showErrorNotificationWhenArticleLoadingFail() {
        // Given a resumed activity

        // When update loading state to 'not loading' containing a retriable error
        val testError = AppError("error",true)
        loading.value = LoadState.Error(testError)
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then activity should display a snack bar with error message
        onView(withId(R.id.snackbar_text))
            .check(matches(
                allOf(
                    withText(testError.cause),
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
    fun hideErrorNotificationWhenArticleLoading() {
        // Given a resumed activity with failed loading state
        loading.value = LoadState.Error(AppError("error",true))
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // When update loading state to 'loading'
        loading.value = LoadState.Loading
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then activity should hide the error notification
        onView(withId(R.id.snackbar_text))
            .check(doesNotExist())
    }

    @Test
    fun retryArticleLoadingWhenLoadingFailWithRetriableError() {
        // Test case fixture
        every { viewModel.reload() } returns Unit

        // Given a resumed activity with failed retriable loading state
        loading.value = LoadState.Error(AppError("error",true))
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // When user click on snackbar action to retry loading
        onView(withId(R.id.snackbar_action))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then activity should ask view model to reload article
        verify { viewModel.reload() }
    }

    @Test
    fun showEmptyAppBarTitle() {
        // Given a resumed activity

        // Then activity should show an empty title in appbar
        scenario.onActivity { assertThat(it.toolbar.title).isEqualTo("") }
    }

    @Test
    fun showUpNavigationInAppBar() {
        // Given a resumed activity

        // Then activity should show up navigation button in its appbar
        onView(withContentDescription(R.string.abc_action_bar_up_description))
            .check(matches(isDisplayed()))
    }

    @Test
    @Config(qualifiers = "land")
    fun showLandUiWhenDeviceInLandOrientation() {
        // Given article activity is resumed in device set in land orientation

        // Then activity should display land layout
        onView(withId(R.id.article_root_land))
            .check(matches(isDisplayed()))
    }
}