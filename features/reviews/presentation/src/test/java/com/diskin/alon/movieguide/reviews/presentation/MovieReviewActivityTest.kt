package com.diskin.alon.movieguide.reviews.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.diskin.alon.movieguide.common.presentation.ImageLoader
import com.diskin.alon.movieguide.common.presentation.ViewData
import com.diskin.alon.movieguide.common.presentation.ViewDataError
import com.diskin.alon.movieguide.reviews.presentation.controller.MovieReviewActivity
import com.diskin.alon.movieguide.reviews.presentation.controller.TrailersAdapter.MovieTrailerViewHolder
import com.diskin.alon.movieguide.reviews.presentation.data.MovieReview
import com.diskin.alon.movieguide.reviews.presentation.viewmodel.MovieReviewViewModel
import com.google.common.truth.Truth.assertThat
import dagger.android.AndroidInjection
import io.mockk.*
import kotlinx.android.synthetic.main.activity_movie_review.*
import org.hamcrest.CoreMatchers.allOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowToast

/**
 * [MovieReviewActivity] hermetic ui test.
 */
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@SmallTest
@Config(sdk = [28])
class MovieReviewActivityTest {

    // Test subject
    private lateinit var scenario: ActivityScenario<MovieReviewActivity>

    // Collaborators
    private val viewModel: MovieReviewViewModel = mockk()

    // Stub data
    private val movieReview = MutableLiveData<ViewData<MovieReview>>()

    @Before
    fun setUp() {
        // Mock out dagger
        mockkStatic(AndroidInjection::class)
        val slot  = slot<MovieReviewActivity>()
        every { AndroidInjection.inject(capture(slot)) } answers {
            slot.captured.viewModel = viewModel
        }

        // Stub collaborators
        every { viewModel.movieReview } returns movieReview

        // Launch activity under test
        scenario = ActivityScenario.launch(MovieReviewActivity::class.java)
    }

    @Test
    fun displayMovieReview() {
        // Test case fixture
        mockkObject(ImageLoader)

        // Given a resumed activity

        // When view model update review state
        val review = createTestReview()
        movieReview.value = ViewData.Data(review)
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then activity should display review data in layout
        onView(withId(R.id.title))
            .check(matches(withText(review.title)))

        onView(withId(R.id.rating))
            .check(matches(withText(review.rating)))

        onView(withId(R.id.genre))
            .check(matches(withText(review.genres)))

        onView(withId(R.id.release_date))
            .check(matches(withText(review.releaseDate)))

        onView(withId(R.id.summary))
            .check(matches(withText(review.summary)))

        onView(withId(R.id.review))
            .check(matches(withText(review.review)))

        verify { ImageLoader.loadIntoImageView(any(),review.backDropImageUrl) }

        review.trailers.forEach { trailer ->
            verify { ImageLoader.loadIntoImageView(any(),trailer.thumbnailUrl) }
        }
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
    fun navToPrevDestinationWhenUserNavigateUp() {
        // Given a resumed activity

        // When user navigate uo from activity
        onView(withContentDescription(R.string.abc_action_bar_up_description))
            .perform(click())

        // Then activity should navigate to prev app graph destination
        // TODO
    }

    @Test
    fun showProgressBarWhenReviewIsUpdating() {
        // Given a resumed activity

        // When view model updating review data
        movieReview.value = ViewData.Updating()
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then activity should show progress bar
        onView(withId(R.id.progress_bar))
            .check(matches(isDisplayed()))
    }

    @Test
    fun hideProgressBarWhenReviewIsNotUpdating() {
        // Given a resumed activity

        // When view model review data available
        val review = createTestReview()
        movieReview.value = ViewData.Data(review)
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then activity should hide progress bar
        onView(withId(R.id.progress_bar))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))

        // TODO verify for error state
    }

    @Test
    @Config(qualifiers = "land")
    fun showLandUiWhenDeviceInLandOrientation() {
        // Given article activity is resumed in device set in land orientation

        // Then activity should display land layout
        onView(withId(R.id.root_review__land))
            .check(matches(isDisplayed()))
    }

    @Test
    fun showErrorNotificationWhenReviewErrorOccur() {
        // Given a resumed activity

        // When review ui state related error happen
        val error = ViewDataError.NoTRetriable("message")
        movieReview.value = ViewData.Error(error)
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then activity should show snackbar with error message
        onView(withId(R.id.snackbar_text))
            .check(matches(
                allOf(
                    withText(error.reason),
                    isDisplayed()
                )
            ))
    }

    @Test
    fun provideRetryWhenRetriableReviewErrorOccur() {
        // Test fixture
        val retryAction: () -> (Unit) = mockk()

        every { retryAction.invoke() } returns Unit
        // Given a resumed activity

        // When review ui state related retriable error happen
        val error = ViewDataError.Retriable("message",retryAction)
        movieReview.value = ViewData.Error(error)
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then activity should show retry option
        onView(withId(R.id.snackbar_action))
            .check(matches(
                allOf(
                    withText(R.string.action_retry),
                    isDisplayed()
                )
            ))

        // When user selects to retry
        onView(withId(R.id.snackbar_action))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then activity should invoke error retry action
        verify { retryAction.invoke() }
    }

    @Test
    fun hideReviewErrorNotificationWhenNoErrorExist() {
        // Given a resumed activity

        // When review ui state related retriable error happen
        movieReview.value = ViewData.Error(ViewDataError.NoTRetriable(""))
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // And review is updated
        movieReview.value = ViewData.Updating()
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then activity should hide error snackbar
        onView(withId(R.id.snackbar_text))
            .check(ViewAssertions.doesNotExist())
    }

    @Test
    fun shareMovieWebPageWhenUserShareReview() {
        // Test case fixture
        Intents.init()

        // Given a resumed activity with displayed review
        val review = createTestReview()
        movieReview.value = ViewData.Data(review)
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // When user clicks on the share menu option
        onView(withId(R.id.action_share))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then activity should share movie trailer url via Android Sharesheet
        Intents.intended(hasAction(Intent.ACTION_CHOOSER))
        Intents.intended(hasExtraWithKey(Intent.EXTRA_INTENT))

        val intent = Intents.getIntents().first().extras?.get(Intent.EXTRA_INTENT) as Intent
        val context = getApplicationContext<Context>()!!

        assertThat(intent.type).isEqualTo(context.getString(R.string.mime_type_text))
        assertThat(intent.getStringExtra(Intent.EXTRA_TEXT))
            .isEqualTo(review.webUrl)

        Intents.release()
    }

    @Test
    fun disallowSharingWhenNoReviewDataShown() {
        // Given a resumed activity without displayed review

        // And user clicks on the share button
        onView(withId(R.id.action_share))
            .perform(click())

        // Then activity should notify user that sharing action is unavailable
        val toastMessage = getApplicationContext<Context>()
            .getString(R.string.title_action_not_available)

        assertThat(ShadowToast.getTextOfLatestToast().toString()).isEqualTo(toastMessage)
    }

    @Test
    fun openTrailerLinkWithDeviceWhenTrailerSelectedFromList() {
        // Test case fixture
        Intents.init()

        // Given a resumed activity with displayed review
        val review = createTestReview()
        movieReview.value = ViewData.Data(review)
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        scenario.onActivity { it.appBar.setExpanded(false,false) }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // When user clicks on first listed trailer
        onView(withId(R.id.trailers))
            .perform(actionOnItemAtPosition<MovieTrailerViewHolder>(0,click()))
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then activity should open link to view trailer via Android intent resolver ui
        Intents.intended(hasAction(Intent.ACTION_CHOOSER))

        val intent = Intents.getIntents().first().extras?.get(Intent.EXTRA_INTENT) as Intent

        assertThat(intent.action).isEqualTo(Intent.ACTION_VIEW)
        assertThat(intent.data).isEqualTo(Uri.parse(review.trailers.first().url))

        Intents.release()
    }
}