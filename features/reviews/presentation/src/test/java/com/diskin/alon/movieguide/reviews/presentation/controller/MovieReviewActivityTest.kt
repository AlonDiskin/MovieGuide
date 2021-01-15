package com.diskin.alon.movieguide.reviews.presentation.controller

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelLazy
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
import com.diskin.alon.movieguide.common.presentation.ErrorViewData
import com.diskin.alon.movieguide.common.presentation.ImageLoader
import com.diskin.alon.movieguide.common.presentation.UpdateViewData
import com.diskin.alon.movieguide.reviews.presentation.R
import com.diskin.alon.movieguide.reviews.presentation.controller.TrailersAdapter.MovieTrailerViewHolder
import com.diskin.alon.movieguide.reviews.presentation.createFavoritedMovieReview
import com.diskin.alon.movieguide.reviews.presentation.createReview
import com.diskin.alon.movieguide.reviews.presentation.data.MovieReview
import com.diskin.alon.movieguide.reviews.presentation.viewmodel.MovieReviewViewModel
import com.google.common.truth.Truth.assertThat
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
    private val movieReview = MutableLiveData<MovieReview>()
    private val reviewUpdate = MutableLiveData<UpdateViewData>()
    private val reviewError = MutableLiveData<ErrorViewData>()

    @Before
    fun setUp() {
        // Stub view model creation with test mock
        mockkConstructor(ViewModelLazy::class)
        every { anyConstructed<ViewModelLazy<MovieReviewViewModel>>().value } returns viewModel

        // Stub collaborators
        every { viewModel.movieReview } returns movieReview
        every { viewModel.reviewUpdate } returns reviewUpdate
        every { viewModel.reviewError } returns reviewError

        // Launch activity under test
        scenario = ActivityScenario.launch(MovieReviewActivity::class.java)
    }

    @Test
    fun displayMovieReview() {
        // Test case fixture
        mockkObject(ImageLoader)

        // Given a resumed activity

        // When view model update review state
        val review = createReview()
        movieReview.value = review
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

        onView(withId(R.id.review_text))
            .check(matches(withText(review.review)))

        verify { ImageLoader.loadIntoImageView(any(), review.backDropImageUrl) }

        review.trailers.forEach { trailer ->
            verify { ImageLoader.loadIntoImageView(any(), trailer.thumbnailUrl) }
        }

        onView(withId(R.id.action_favoriting))
            .check(
                matches(
                    withContentDescription(
                        if (review.favorite)
                            R.string.title_action_unfavorite_movie
                        else
                            R.string.title_action_favorite_movie
                    )
                )
            )
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
        reviewUpdate.value = UpdateViewData.Update
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then activity should show progress bar
        onView(withId(R.id.progress_bar))
            .check(matches(isDisplayed()))
    }

    @Test
    fun hideProgressBarWhenReviewIsNotUpdating() {
        // Given a resumed activity

        // When view model review data available
        reviewUpdate.value = UpdateViewData.EndUpdate
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then activity should hide progress bar
        onView(withId(R.id.progress_bar))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
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
        val error = ErrorViewData.NotRetriable("message")
        reviewError.value = error
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then activity should show snackbar with error message
        onView(withId(R.id.snackbar_text))
            .check(
                matches(
                    allOf(
                        withText(error.reason),
                        isDisplayed()
                    )
                )
            )
    }

    @Test
    fun provideRetryWhenRetriableReviewErrorOccur() {
        // Test fixture
        val retryAction: () -> (Unit) = mockk()

        every { retryAction.invoke() } returns Unit
        // Given a resumed activity

        // When review ui state related retriable error happen
        val error = ErrorViewData.Retriable("message", retryAction)
        reviewError.value = error
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then activity should show retry option
        onView(withId(R.id.snackbar_action))
            .check(
                matches(
                    allOf(
                        withText(R.string.action_retry),
                        isDisplayed()
                    )
                )
            )

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
        reviewError.value = ErrorViewData.NoError
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
        val review = createReview()
        movieReview.value = review
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
        val review = createReview()
        movieReview.value = review
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        scenario.onActivity { it.appBar.setExpanded(false, false) }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // When user clicks on first listed trailer
        onView(withId(R.id.trailers))
            .perform(actionOnItemAtPosition<MovieTrailerViewHolder>(0, click()))
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then activity should open link to view trailer via Android intent resolver ui
        Intents.intended(hasAction(Intent.ACTION_CHOOSER))

        val intent = Intents.getIntents().first().extras?.get(Intent.EXTRA_INTENT) as Intent

        assertThat(intent.action).isEqualTo(Intent.ACTION_VIEW)
        assertThat(intent.data).isEqualTo(Uri.parse(review.trailers.first().url))

        Intents.release()
    }

    @Test
    fun favoriteMovieWhenUserSelectToUnFavoriteReviewedMovie() {
        // Test case fixture
        every { viewModel.favoriteReviewedMovie() } returns Unit

        // Given a resumed activity with displayed review of un favorited movie
        movieReview.value = createReview()
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // When user select to favorite movie
        onView(withId(R.id.action_favoriting))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then activity should ask view model to favorite movie
        verify { viewModel.favoriteReviewedMovie() }
    }

    @Test
    fun uFavoriteMovieWhenUserSelectForFavoriteReviewedMovie() {
        // Test case fixture
        every { viewModel.unFavoriteReviewedMovie() } returns Unit

        // Given a resumed activity with displayed review  of favorite movie
        movieReview.value = createFavoritedMovieReview()
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // When user select to un favorite reviewed movie
        onView(withId(R.id.action_favoriting))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then activity should ask view model to un favorite movie
        verify { viewModel.unFavoriteReviewedMovie() }
    }

    @Test
    fun disableMovieFavoritingActionWhenReviewNotUpdated() {
        // Given a resumed activity without a shown review

        // Then favoriting menu item should be disabled
        scenario.onActivity {
            val item = it.toolbar.menu.findItem(R.id.action_favoriting)
            assertThat(item.isEnabled).isFalse()
        }
    }

    @Test
    fun enableMovieFavoritingActionWhenReviewNotUpdated() {
        // Given a resumed activity with displayed review
        movieReview.value = createReview()
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then favoriting menu item should be enabled
        scenario.onActivity {
            val item = it.toolbar.menu.findItem(R.id.action_favoriting)
            assertThat(item.isEnabled).isTrue()
        }
    }
}