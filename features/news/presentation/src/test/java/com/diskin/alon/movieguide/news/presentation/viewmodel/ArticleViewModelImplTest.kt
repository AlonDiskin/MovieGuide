package com.diskin.alon.movieguide.news.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.diskin.alon.movieguide.common.appservices.AppError
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.presentation.Model
import com.diskin.alon.movieguide.common.presentation.ErrorViewData
import com.diskin.alon.movieguide.common.presentation.UpdateViewData
import com.diskin.alon.movieguide.news.presentation.data.Article
import com.diskin.alon.movieguide.news.presentation.data.ArticleModelRequest
import com.diskin.alon.movieguide.news.presentation.data.BookmarkingModelRequest
import com.diskin.alon.movieguide.news.presentation.data.UnBookmarkingModelRequest
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test

/**
 * [ArticleViewModelImpl] unit test.
 */
class ArticleViewModelImplTest {

    companion object {

        @JvmStatic
        @BeforeClass
        fun setupClass() {
            // Set Rx framework for testing
            RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        }
    }

    // Lifecycle testing rule
    @JvmField
    @Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Test subject
    private lateinit var viewModel: ArticleViewModelImpl

    // Collaborators
    private val model: Model = mockk()
    private val savedState: SavedStateHandle = SavedStateHandle()

    // Stub data
    private val modelArticleSubject = PublishSubject.create<Result<Article>>()
    private val articleId = "id"

    @Before
    fun setUp() {
        // Stub collaborators
        every { model.execute(any<ArticleModelRequest>()) } returns modelArticleSubject
        savedState.set(ArticleViewModelImpl.KEY_ARTICLE_ID,articleId)

        // Init subject
        viewModel = ArticleViewModelImpl(model,savedState)
    }

    @Test
    fun initUpdateViewDataAsUpdatingWhenCreated() {
        // Given an initialized view model

        // Then view model should init UPDATE view data in updating state
        assertThat(viewModel.update.value).isEqualTo(UpdateViewData.Update)
    }

    @Test(expected = IllegalArgumentException::class)
    fun throwExceptionWhenCreatedWithoutArticleId() {
        // Test case fixture

        // When view model is created without article id (null value)
        ArticleViewModelImpl(model, SavedStateHandle())

        // Then view model should throw a IllegalArgumentException
    }

    @Test
    fun fetchModelArticleWhenCreated() {
        // Given an initialized view model

        // Then view model should subscribe to model article
        verify { model.execute(ArticleModelRequest(articleId)) }
    }

    @Test
    fun updateArticleViewDataAndUpdatingViewDataWhenModelArticleUpdates() {
        // Given an initialized view model

        // When model update article
        val article: Article = mockk()
        modelArticleSubject.onNext(Result.Success(article))

        // Then view model should update article view data
        assertThat(viewModel.article.value).isEqualTo(article)
        assertThat(viewModel.update.value).isEqualTo(UpdateViewData.EndUpdate)
    }

    @Test
    fun updateErrorViewDataWhenModelArticleErrors() {
        // Given an initialized view model

        // When model article updates an error
        val error = AppError("message",false)
        modelArticleSubject.onNext(Result.Error(error))

        // Then view model should update error view data with error accordingly
        assertThat(viewModel.error.value).isEqualTo(ErrorViewData.NotRetriable(error.description))
    }

    @Test
    fun bookmarkModelArticleWhenViewBookmarksArticle() {
        //  Test case fixture
        every { model.execute(any<BookmarkingModelRequest>()) } returns Single.just(Result.Success(Unit))

        // Given

        // When
        viewModel.bookmark()

        // Then
        verify { model.execute(BookmarkingModelRequest(articleId)) }
        assertThat(viewModel.update.value).isEqualTo(UpdateViewData.Update)
    }

    @Test
    fun unBookmarkModelArticleWhenViewBookmarksArticle() {
        //  Test case fixture
        every { model.execute(any<BookmarkingModelRequest>()) } returns Single.just(Result.Success(Unit))

        // Given

        // When
        viewModel.unBookmark()

        // Then
        verify { model.execute(UnBookmarkingModelRequest(listOf(articleId))) }
        assertThat(viewModel.update.value).isEqualTo(UpdateViewData.Update)
    }
}