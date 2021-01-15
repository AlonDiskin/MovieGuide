package com.diskin.alon.movieguide.reviews.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.diskin.alon.movieguide.common.appservices.AppError
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.presentation.ErrorViewData
import com.diskin.alon.movieguide.common.presentation.Model
import com.diskin.alon.movieguide.common.presentation.RxViewModel
import com.diskin.alon.movieguide.common.presentation.UpdateViewData
import com.diskin.alon.movieguide.reviews.presentation.data.FavoriteMovieModelRequest
import com.diskin.alon.movieguide.reviews.presentation.data.MovieReview
import com.diskin.alon.movieguide.reviews.presentation.data.ReviewModelRequest
import com.diskin.alon.movieguide.reviews.presentation.data.UnFavoriteMovieModelRequest
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.SingleSubject
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test

/**
 * [MovieReviewViewModel] unit test class.
 */
class MovieReviewViewModelTest {

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
    private lateinit var viewModel: MovieReviewViewModel

    // Collaborators
    private val model: Model = mockk()
    private val savedStateHandle = SavedStateHandle()

    // Stub data
    private val modelReview = BehaviorSubject.create<Result<MovieReview>>()
    private val movieId = "id"

    @Before
    fun setUp() {
        // Stub collaborators
        every { model.execute(any<ReviewModelRequest>()) } returns modelReview
        savedStateHandle.set(MovieReviewViewModel.KEY_MOVIE_ID,movieId)

        // Initialize
        viewModel = MovieReviewViewModel(model,savedStateHandle)
    }

    @Test(expected = IllegalArgumentException::class)
    fun throwExceptionWhenCreatedWithoutMovieIdState() {
        // Given a non initialized view model

        // When view model is initialized without review id value in saved state handle param
        viewModel = MovieReviewViewModel(mockk(), SavedStateHandle())

        // Then view model should throw an IllegalArgumentException
    }

    @Test(expected = IllegalArgumentException::class)
    fun throwExceptionWhenCreatedWithMovieIdNullState() {
        // Given a non initialized view model

        // When view model is initialized with review id null value in saved state handle param
        val savedState = SavedStateHandle().apply {
            set(MovieReviewViewModel.KEY_MOVIE_ID,null)
        }
        viewModel = MovieReviewViewModel(mockk(),savedState)

        // Then view model should throw an IllegalArgumentException
    }

    @Test
    fun requestReviewFromModelAndUpdateViewDataWhenCreated() {
        // Given an initialized view model

        // Then view model should subscribe to model review updates
        verify { model.execute(ReviewModelRequest(movieId)) }

        // When model update review data
        val review = mockk<MovieReview>()
        modelReview.onNext(Result.Success(review))

        // Then view model should update view review state
        assertThat(viewModel.movieReview.value).isEqualTo(review)
    }

    @Test
    fun addModelRxSubscriptionsToContainer() {
        // Given an initialized

        // Then view model should add model review subscription to disposable container
        val field = RxViewModel::class.java.getDeclaredField("container")
        field.isAccessible = true
        val disposable = field.get(viewModel) as CompositeDisposable
        assertThat(disposable.size()).isEqualTo(2)
    }

    @Test
    fun updateReviewErrorViewDataWhenModelReviewUpdateFail() {
        // Given an initialized view model

        // When model review update fail
        val appError = AppError("message",false)
        modelReview.onNext(Result.Error(appError))

        // Then view model should update review error view data
        assertThat(viewModel.reviewError.value).isEqualTo(ErrorViewData.NotRetriable(appError.description))
    }

    @Test
    fun favoriteModelMovieWhenFavoritingReviewedMovie() {
        //  Test case fixture
        val modelSubject = SingleSubject.create<Result<Unit>>()
        every { model.execute(any<FavoriteMovieModelRequest>()) } returns modelSubject

        // Given an initialized view model

        // When view model s asked to favorite a the reviewed movie
        viewModel.favoriteReviewedMovie()

        // Then view model should set review update view data state as 'updating'
        assertThat(viewModel.reviewUpdate.value).isEqualTo(UpdateViewData.Update)

        // And ask model to favorite the movie
        verify { model.execute(FavoriteMovieModelRequest(movieId)) }

        // When model completes operation
        modelSubject.onSuccess(Result.Success(Unit))

        // Then view model should set review update view data state as 'not updating'
        assertThat(viewModel.reviewUpdate.value).isEqualTo(UpdateViewData.EndUpdate)
    }

    @Test
    fun updateReviewErrorViewDataWhenFavoritingModelMovieFail() {
        //  Test case fixture
        val modelSubject = SingleSubject.create<Result<Unit>>()
        every { model.execute(any<FavoriteMovieModelRequest>()) } returns modelSubject

        // Given an initialized view model

        // When view model asked to favorite the reviewed movie
        viewModel.favoriteReviewedMovie()

        // And model fail operation
        val appError = AppError("message",false)
        modelSubject.onSuccess(Result.Error(appError))

        // Then view model should update review error view data
        assertThat(viewModel.reviewError.value).isEqualTo(ErrorViewData.NotRetriable(appError.description))
    }

    @Test
    fun unFavoriteModelMovieWhenUnFavoriteReviewedMovie() {
        //  Test case fixture
        val modelSubject = SingleSubject.create<Result<Unit>>()
        every { model.execute(any<UnFavoriteMovieModelRequest>()) } returns modelSubject

        // Given an initialized view model

        // When view model asked to un favorite a the reviewed movie
        viewModel.unFavoriteReviewedMovie()

        // Then view model should set review update view data state as 'updating'
        assertThat(viewModel.reviewUpdate.value).isEqualTo(UpdateViewData.Update)

        // And ask model to un favorite the movie
        verify { model.execute(UnFavoriteMovieModelRequest(movieId)) }

        // When model completes operation
        modelSubject.onSuccess(Result.Success(Unit))

        // Then view model should set review update view data state as 'not updating'
        assertThat(viewModel.reviewUpdate.value).isEqualTo(UpdateViewData.EndUpdate)
    }

    @Test
    fun updateReviewErrorViewDataWhenUnFavoritingModelMovieFail() {
        //  Test case fixture
        val modelSubject = SingleSubject.create<Result<Unit>>()
        every { model.execute(any<UnFavoriteMovieModelRequest>()) } returns modelSubject

        // Given an initialized view model

        // When view model asked to un favorite the reviewed movie
        viewModel.favoriteReviewedMovie()

        // And model fail operation
        val appError = AppError("message",false)
        modelSubject.onSuccess(Result.Error(appError))

        // Then view model should update review error view data
        assertThat(viewModel.reviewError.value).isEqualTo(ErrorViewData.NotRetriable(appError.description))
    }
}