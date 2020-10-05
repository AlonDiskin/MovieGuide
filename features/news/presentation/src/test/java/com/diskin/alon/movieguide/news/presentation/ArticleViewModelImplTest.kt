package com.diskin.alon.movieguide.news.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.diskin.alon.movieguide.common.appservices.UseCase
import com.diskin.alon.movieguide.common.presentation.RxViewModel
import com.diskin.alon.movieguide.news.appservices.model.ArticleDto
import com.diskin.alon.movieguide.news.appservices.model.ArticleRequest
import com.diskin.alon.movieguide.news.presentation.model.Article
import com.diskin.alon.movieguide.news.presentation.viewmodel.ArticleViewModelImpl
import com.diskin.alon.movieguide.news.presentation.viewmodel.ArticleViewModelImpl.Companion.KEY_ARTICLE_ID
import com.diskin.alon.movieguide.news.presentation.viewmodel.mapArticleDto
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import io.reactivex.Observable
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
    private val useCase = mockk<UseCase<ArticleRequest,Observable<ArticleDto>>>()
    private val savedState: SavedStateHandle = mockk()

    // Stub data
    private val useCaseResultSubject = PublishSubject.create<ArticleDto>()
    private val articleId = "id"
    private val testArticle = mockk<Article>()

    // Capture args
    private val useCaseSlot = slot<ArticleRequest>()

    @Before
    fun setUp() {
        // Mock out Mapper util
        mockkStatic("com.diskin.alon.movieguide.news.presentation.viewmodel.MapperKt")

        // Stub collaborators
        every { useCase.execute(capture(useCaseSlot)) } returns useCaseResultSubject
        every { savedState.get<String>(KEY_ARTICLE_ID) } returns articleId
        every { mapArticleDto(any()) } returns testArticle

        // Init subject
        viewModel = ArticleViewModelImpl(useCase,savedState)
    }

    @Test
    fun fetchArticleWhenCreated() {
        // Given an initialized view model

        // Then view model should subscribe to article use case with state handle param
        verify { useCase.execute(ArticleRequest(articleId)) }
        val field = RxViewModel::class.java.getDeclaredField("disposable")
        field.isAccessible = true
        val disposable = field.get(viewModel) as CompositeDisposable

        assertThat(disposable.size()).isEqualTo(1)

        // When use case update article result
        val useCaseResult = mockk<ArticleDto>()
        this.useCaseResultSubject.onNext(useCaseResult)

        // Then view model should map use case result
        verify { mapArticleDto(useCaseResult) }

        // And update article state with mapped result
        assertThat(viewModel.article.value).isEqualTo(testArticle)
    }
}