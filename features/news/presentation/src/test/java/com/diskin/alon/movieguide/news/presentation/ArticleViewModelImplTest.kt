package com.diskin.alon.movieguide.news.presentation

import android.content.res.Resources
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.diskin.alon.movieguide.common.appservices.AppError
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.appservices.UseCase
import com.diskin.alon.movieguide.common.common.Mapper
import com.diskin.alon.movieguide.common.presentation.LoadState
import com.diskin.alon.movieguide.common.presentation.RxViewModel
import com.diskin.alon.movieguide.news.appservices.model.ArticleDto
import com.diskin.alon.movieguide.news.appservices.model.ArticleRequest
import com.diskin.alon.movieguide.news.presentation.model.Article
import com.diskin.alon.movieguide.news.presentation.viewmodel.ArticleViewModelImpl
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
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
    private val useCase: UseCase<ArticleRequest,Observable<Result<ArticleDto>>> = mockk()
    private val articleMapper: Mapper<ArticleDto, Article> = mockk()
    private val savedState: SavedStateHandle = mockk()
    private val resources: Resources = mockk()

    // Stub data
    private val useCaseResultSubject = PublishSubject.create<Result<ArticleDto>>()
    private val articleId = "id"
    private val testArticle = mockk<Article>()

    // Capture args
    private val useCaseSlot = slot<ArticleRequest>()

    @Before
    fun setUp() {
        // Stub collaborators
        every { useCase.execute(capture(useCaseSlot)) } returns useCaseResultSubject
        every { savedState.get<String>("article_id") } returns articleId
        every { resources.getString(R.string.key_article_id) } returns "article_id"
        every { articleMapper.map(any()) } returns testArticle

        // Init subject
        viewModel = ArticleViewModelImpl(useCase,articleMapper,savedState,resources)
    }

    @Test
    fun initLoadingStateAsLoadingWhenCreated() {
        // Given an initialized view model

        // Then view model should init loading state as loading when created
        assertThat(viewModel.loading.value).isEqualTo(LoadState.Loading)
    }

    @Test(expected = IllegalArgumentException::class)
    fun throwExceptionWhenCreatedWithoutArticleId() {
        // Test case fixture
        every { savedState.get<String>("article_id") } returns null

        // When view model is created without article id (null value)
        ArticleViewModelImpl(useCase,articleMapper,savedState,resources)

        // Then view model should throw a IllegalArgumentException
    }

    @Test
    fun observeModelArticleWhenCreated() {
        // Given an initialized view model

        // Then view model should subscribe to article use case with state handle param
        verify { useCase.execute(ArticleRequest(articleId)) }

        // And add subscription to disposable container
        val field = RxViewModel::class.java.getDeclaredField("container")
        field.isAccessible = true
        val disposable = field.get(viewModel) as CompositeDisposable
        assertThat(disposable.size()).isEqualTo(1)
    }

    @Test
    fun updateArticleAndLoadingStateWhenModelArticleUpdates() {
        // Given an initialized view model that subscribed to use case that emit article results

        // When use case successfully update article state
        val articleDto = mockk<ArticleDto>()
        val useCaseResult = Result.Success(articleDto)
        this.useCaseResultSubject.onNext(useCaseResult)

        // Then view model should ask article mapper to map article result dto
        verify { articleMapper.map(articleDto) }

        // And view model should update loading state as 'not loading'
        assertThat(viewModel.loading.value).isEqualTo(LoadState.Success)

        // And update its live data article state
        assertThat(viewModel.article.value).isEqualTo(testArticle)
    }

    @Test
    fun updateLoadingStateWhenModelArticleNotifyError() {
        // Given an initialized view model that subscribed to use case that emit a app error

        // When use case send an error notifies of an error
        val appError = mockk<AppError>()
        val useCaseResult = Result.Error<ArticleDto>(appError)
        this.useCaseResultSubject.onNext(useCaseResult)

        // Then view model should update loading state as 'error'
        assertThat(viewModel.loading.value).isEqualTo(LoadState.Error(appError))
    }

    @Test
    fun updateLoadingStateWhenAskedToReload() {
        // Given an initialized view model

        // When view model is asked to  reload its article state
        viewModel.reload()

        // Then view model should update loading state as 'loading'
        assertThat(viewModel.loading.value).isEqualTo(LoadState.Loading)
    }

    @Test
    fun fetchModelArticleWhenAskedToReload() {
        // Given an initialized view model

        // When view model is asked to reload article
        viewModel.reload()

        // Then view model should re execute article use case with id state
        verify(exactly = 2) { useCase.execute(ArticleRequest(articleId)) }
    }
}