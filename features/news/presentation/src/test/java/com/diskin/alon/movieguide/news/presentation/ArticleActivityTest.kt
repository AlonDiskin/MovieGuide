package com.diskin.alon.movieguide.news.presentation

import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.diskin.alon.movieguide.news.presentation.controller.ArticleActivity
import com.diskin.alon.movieguide.news.presentation.model.Article
import com.diskin.alon.movieguide.news.presentation.viewmodel.ArticleViewModel
import com.google.common.truth.Truth.assertThat
import dagger.android.AndroidInjection
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
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

        // Launch activity under test
        scenario = ActivityScenario.launch(ArticleActivity::class.java)
    }

    @Test
    fun shareArticleWhenArticleSharingSelected() {
        // Test case fixture
        Intents.init()

        // Given a resumed activity with article state in view model
        val testArticle = Article("url")
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
    fun notifySharingUnavailableWhenArticleStateNull() {
        // Given a resumed activity with article null state in view model

        // And user clicks on the share button
        onView(withId(R.id.action_share))
            .perform(click())

        // Then activity should notify user that sharing action is unavailable
        val toastMessage = ApplicationProvider.getApplicationContext<Context>()
            .getString(R.string.title_action_not_available)

        assertThat(ShadowToast.getTextOfLatestToast().toString()).isEqualTo(toastMessage)
    }
}