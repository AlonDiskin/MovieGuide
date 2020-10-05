package com.diskin.alon.movieguide.news.presentation

import android.content.Context
import android.content.Intent
import android.os.Looper
import android.view.View
import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.MutableLiveData
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.rxjava2.RxPagingSource
import androidx.paging.rxjava2.observable
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.diskin.alon.movieguide.common.presentation.ImageLoader
import com.diskin.alon.movieguide.common.uitesting.RecyclerViewMatcher.withRecyclerView
import com.diskin.alon.movieguide.common.uitesting.swipeToRefresh
import com.diskin.alon.movieguide.news.presentation.controller.MoviesHeadlinesFragment
import com.diskin.alon.movieguide.news.presentation.controller.NewsHeadlinesAdapter
import com.diskin.alon.movieguide.news.presentation.controller.NewsHeadlinesAdapter.NewsHeadlineViewHolder
import com.diskin.alon.movieguide.news.presentation.model.NewsHeadline
import com.diskin.alon.movieguide.news.presentation.viewmodel.MoviesHeadlinesViewModel
import com.google.common.truth.Truth.assertThat
import dagger.android.support.AndroidSupportInjection
import io.mockk.*
import io.reactivex.Single
import kotlinx.android.synthetic.main.fragment_movies_headlines.*
import org.hamcrest.CoreMatchers.allOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

/**
 * [MoviesHeadlinesFragment] hermetic ui tests.
 */
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@SmallTest
@Config(sdk = [28])
class MoviesHeadlinesFragmentTest {

    // System under test
    private lateinit var scenario: FragmentScenario<MoviesHeadlinesFragment>

    // Collaborators
    private val viewModel: MoviesHeadlinesViewModel = mockk()

    // Stub data
    private val headlines = MutableLiveData<PagingData<NewsHeadline>>()

    @Before
    fun setUp() {
        // Mock out dagger di
        val fragmentSlot = slot<MoviesHeadlinesFragment>()
        mockkStatic(AndroidSupportInjection::class)
        every { AndroidSupportInjection.inject(capture(fragmentSlot)) } answers {
            fragmentSlot.captured.viewModel = viewModel
        }

        // Stub mocked view model
        every { viewModel.headlines } returns headlines

        // Launch fragment under test with specified theme for material widgets usage
        scenario = FragmentScenario.launchInContainer(
            MoviesHeadlinesFragment::class.java,
            null,
            R.style.AppTheme,
            null
        )
    }

    @Test
    fun showHeadlines() {
        // Test case fixture
        mockkObject(ImageLoader)

        // Given a resumed fragment

        // When view model update headlines data
        val testHeadlines = createNewsHeadlines()
        headlines.value = PagingData.from(testHeadlines)

        // Then fragment should show headlines data in layout
        testHeadlines.forEachIndexed { index, newsHeadline ->
            // Scroll to expected headline layout position
            onView(withId(R.id.headlines))
                .perform(scrollToPosition<NewsHeadlineViewHolder>(index))

            Shadows.shadowOf(Looper.getMainLooper()).idle()

            // Check list item at scrolled position display expected data
            onView(withRecyclerView(R.id.headlines).atPosition(index))
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

            // Verify news headline image was loaded
            verify { ImageLoader.loadIntoImageView(any(),newsHeadline.imageUrl) }
        }
    }

    @Test
    fun refreshHeadlinesWhenUserSwipeToRefresh() {
        // Test case fixture
        var refreshCounter = 0
        val  paging = Pager(PagingConfig(pageSize = 10)) {
            return@Pager object : RxPagingSource<String, NewsHeadline>() {
                override fun loadSingle(params: LoadParams<String>): Single<LoadResult<String, NewsHeadline>> {
                    if (params is LoadParams.Refresh) {
                        refreshCounter++
                    }
                    return Single.just(createNewsHeadlines())
                        .map { headlines ->
                            LoadResult.Page<String, NewsHeadline>(
                                headlines,
                                null,
                                null
                            )
                        }
                }
            }
        }.observable

        // Given a resumed fragment that observe view model paging sate
        paging.subscribe{ data -> headlines.value = data}

        // When user performs swipe to refresh
        onView(withId(R.id.swipe_refresh))
            .perform(swipeToRefresh())

        // Then fragment should refresh headlines adapter
        assertThat(refreshCounter).isEqualTo(2)
    }

    @Test
    fun showRefreshUiIndicatorWhenHeadlinesRefreshed() {
        // Test case fixture
        var wasRefreshed = false
        scenario.onFragment { fragment ->
            (fragment.headlines.adapter as NewsHeadlinesAdapter)
                .addLoadStateListener { state ->
                    when (state.refresh) {
                        is LoadState.Loading -> wasRefreshed = fragment.swipe_refresh.isRefreshing
                    }
                }
        }

        val paging = Pager(PagingConfig(pageSize = 10)) {
            return@Pager object : RxPagingSource<String, NewsHeadline>() {
                override fun loadSingle(params: LoadParams<String>): Single<LoadResult<String, NewsHeadline>> {
                    return Single.just(createNewsHeadlines())
                        .map { headlines ->
                            LoadResult.Page<String, NewsHeadline>(
                                headlines,
                                null,
                                null
                            )
                        }
                }
            }
        }.observable

        // Given a resumed fragment that observe view model paging sate
        paging.subscribe { headlines.value = it }

        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should display refresh layout indicator upon paging refreshing state change
        assertThat(wasRefreshed).isTrue()
    }

    @Test
    fun showProgressBarWhenHeadlinesAddedPageLoaded() {
        // fixture
        var progressBarShown = false
        scenario.onFragment { fragment ->
            (fragment.headlines.adapter as NewsHeadlinesAdapter)
                .addLoadStateListener { state ->
                    when (state.append) {
                        is LoadState.Loading -> progressBarShown =
                            (fragment.progress_bar.visibility == View.VISIBLE)
                    }
                }
        }
        Pager(PagingConfig(pageSize = 10)) {
            return@Pager object : RxPagingSource<String, NewsHeadline>() {
                override fun loadSingle(params: LoadParams<String>): Single<LoadResult<String, NewsHeadline>> {
                    val key = when(params) {
                        is LoadParams.Refresh -> "key"
                        else -> null
                    }

                    return Single.just(createNewsHeadlines())
                        .map { headlines ->
                            LoadResult.Page(
                                headlines,
                                null,
                                key
                            )
                        }
                }
            }
        }.observable
            .subscribe { headlines.value = it }

        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // When
        scenario.onFragment { fragment ->
            val adapter = fragment.headlines.adapter!!
            val lastPosition = adapter.itemCount

            fragment.headlines.smoothScrollToPosition(lastPosition)
        }

        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then
        assertThat(progressBarShown).isTrue()
    }

    @Test
    fun showNotificationWithRetryActionWhenRecoverableErrorOccurDuringHeadlinesRefresh() {
        // Test case fixture
        val pagingError = Throwable("error message")
        val paging = Pager(PagingConfig(pageSize = 10)) {
            return@Pager object : RxPagingSource<String, NewsHeadline>() {
                override fun loadSingle(params: LoadParams<String>): Single<LoadResult<String, NewsHeadline>> {
                    return Single.just(LoadResult.Error(pagingError))
                }
            }
        }.observable

        // Given a resumed fragment

        // When paging refresh state updates to an error that contains a message
        paging.subscribe { headlines.value = it }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should display a snack bar with error message
        onView(withId(R.id.snackbar_text))
            .check(matches(allOf(
                withText(pagingError.message),
                isDisplayed()
            )))

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
    fun showNotificationWhenUnRecoverablePagingErrorOccur() {
        // Test case fixture
        val pagingError = Throwable()
        val paging = Pager(PagingConfig(pageSize = 10)) {
            return@Pager object : RxPagingSource<String, NewsHeadline>() {
                override fun loadSingle(params: LoadParams<String>): Single<LoadResult<String, NewsHeadline>> {
                    return Single.just(LoadResult.Error(pagingError))
                }
            }
        }.observable

        // Given a resumed fragment

        // When paging state updates to an error that does not contains a message
        paging.subscribe { headlines.value = it }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should display a snack bar with generic error message
        onView(withId(R.id.snackbar_text))
            .check(matches(allOf(
                withText(R.string.unexpected_error),
                isDisplayed()
            )))

        // And do not provide a retry action for failed operation
        onView(withId(R.id.snackbar_action))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun shareHeadlineWhenHeadlineSharingSelected() {
        // Test case fixture
        Intents.init()

        // Given a resumed fragment that display single news headline
        val testHeadlines = createNewsHeadlines()
        headlines.value = PagingData.from(listOf(testHeadlines.first()))
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // When user click on headline share button of first headline
        onView(withId(R.id.shareButton))
            .perform(click())

        // Then fragment should share headline article url via Android Sharesheet
        Intents.intended(IntentMatchers.hasAction(Intent.ACTION_CHOOSER))
        Intents.intended(IntentMatchers.hasExtraWithKey(Intent.EXTRA_INTENT))

        val intent = Intents.getIntents().first().extras?.get(Intent.EXTRA_INTENT) as Intent
        val context = ApplicationProvider.getApplicationContext<Context>()!!

        assertThat(intent.type).isEqualTo(context.getString(R.string.mime_type_text))
        assertThat(intent.getStringExtra(Intent.EXTRA_TEXT))
            .isEqualTo(testHeadlines.first().articleUrl)

        Intents.release()
    }
}