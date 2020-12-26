package com.diskin.alon.movieguide.home.presentation

import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.google.common.truth.Truth.assertThat
import dagger.android.AndroidInjection
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import kotlinx.android.synthetic.main.activity_main.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

/**
 * [MainActivity] hermetic ui test class.
 */
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@MediumTest
@Config(sdk = [28])
class MainActivityTest {

    // System under test
    private lateinit var scenario: ActivityScenario<MainActivity>

    // SUT collaborators
    private val navigator: HomeNavigator = mockk()

    @Before
    fun setUp() {
        // Mock out dagger di
        mockkStatic(AndroidInjection::class)

        // Stub dagger di to inject activity with mock collaborators
        val activitySlot = slot<MainActivity>()
        every { AndroidInjection.inject(capture(activitySlot)) } answers {
            activitySlot.captured.navigator = navigator
        }

        // Stub mocked navigator
        every { navigator.getNewsNavGraph() } returns getTestNewsGraph()
        every { navigator.getReviewsNavGraph() } returns getTestReviewsGraph()
        every { navigator.getSettingsNavGraph() } returns getTestSettingsGraph()
        every { navigator.getBookmarksNavGraph() } returns getTestBookmarksGraph()

        // Launch activity under test
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun showAppNameInToolbar() {
        // Given a resumed activity

        // Then activity should display application name as toolbar title
        scenario.onActivity {
            val toolbar = it.findViewById<Toolbar>(R.id.toolbar)
            val expectedTitle = it.getString(R.string.app_name)

            assertThat(toolbar.title).isEqualTo(expectedTitle)
        }
    }

    @Test
    fun compositeAppFeaturesUi() {
        // Given a resumed activity

        // When user navigates to news feature
        onView(withId(R.id.news))
            .perform(click())

        // Then news feature ui should be displayed in activity layout
        scenario.onActivity {
            val controller = it.nav_host_container.findNavController()

            assertThat(controller.currentDestination!!.id).isEqualTo(R.id.news_placeholder)
        }

        // When user navigates to movies reviews feature
        onView(withId(R.id.reviews))
            .perform(click())

        // Then reviews feature ui should be displayed in activity layout
        scenario.onActivity {
            val controller = it.nav_host_container.findNavController()

            assertThat(controller.currentDestination!!.id).isEqualTo(R.id.reviews_placeholder)
        }

        // When user navigates to settings feature
        onView(withId(R.id.settings))
            .perform(click())

        // Then settings feature ui should be displayed in activity layout
        scenario.onActivity {
            val controller = it.nav_host_container.findNavController()

            assertThat(controller.currentDestination!!.id).isEqualTo(R.id.settings_placeholder)
        }

        // When user navigates to bookmarks feature
        onView(withId(R.id.bookmarks))
            .perform(click())

        // Then bookmarks feature ui should be displayed in activity layout
        scenario.onActivity {
            val controller = it.nav_host_container.findNavController()

            assertThat(controller.currentDestination!!.id).isEqualTo(R.id.bookmarks_placeholder)
        }
    }

    @Test
    fun showNewsFeatureUiAsHome() {
        // Given a resumed activity

        // Then news feature ui should be displayed in activity layout
        scenario.onActivity {
            val controller = it.nav_host_container.findNavController()

            assertThat(controller.currentDestination!!.id).isEqualTo(R.id.news_placeholder)
        }
    }
}