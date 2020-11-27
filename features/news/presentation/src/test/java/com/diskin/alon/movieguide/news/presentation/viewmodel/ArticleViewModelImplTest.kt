package com.diskin.alon.movieguide.news.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.diskin.alon.movieguide.common.appservices.AppError
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.localtesting.WhiteBox
import com.diskin.alon.movieguide.common.presentation.Model
import com.diskin.alon.movieguide.common.presentation.ViewData
import com.diskin.alon.movieguide.news.presentation.data.Article
import com.diskin.alon.movieguide.news.presentation.data.ArticleModelRequest
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.disposables.CompositeDisposable
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
    fun initArticleViewDataAsUpdatingWhenCreated() {
        // Given an initialized view model

        // Then view model should init article view data in updating state
        assertThat(viewModel.article.value).isInstanceOf(ViewData.Updating::class.java)
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

        // And add subscription to disposable container
        val disposable = WhiteBox.getInternalState(viewModel,"container") as CompositeDisposable
        assertThat(disposable.size()).isEqualTo(1)
    }

    @Test
    fun updateArticleViewDataWhenModelArticleUpdates() {
        // Given an initialized view model

        // When model update article
        val article: Article = mockk()
        modelArticleSubject.onNext(Result.Success(article))

        // Then view model should update article view data
        assertThat(viewModel.article.value!!.data).isEqualTo(article)
    }

    @Test
    fun updateArticleViewDataWhenModelArticleErrors() {
        // Given an initialized view model

        // When model article updates an error
        val error = AppError("message",false)
        modelArticleSubject.onNext(Result.Error(error))

        // Then view model should update article view data with error accordingly
        assertThat(viewModel.article.value).isInstanceOf(ViewData.Error::class.java)
        val viewData = viewModel.article.value as ViewData.Error
        assertThat(viewData.error.reason).isEqualTo(error.cause)
    }
}