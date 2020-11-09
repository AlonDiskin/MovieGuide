package com.diskin.alon.movieguide.reviews.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.diskin.alon.movieguide.common.appservices.AppError
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.presentation.Model
import com.diskin.alon.movieguide.common.presentation.RxViewModel
import com.diskin.alon.movieguide.common.presentation.ViewData
import com.diskin.alon.movieguide.common.presentation.ViewDataError
import com.diskin.alon.movieguide.reviews.presentation.data.MovieReview
import com.diskin.alon.movieguide.reviews.presentation.data.ReviewModelRequest
import com.diskin.alon.movieguide.reviews.presentation.viewmodel.MovieReviewViewModelImpl
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test

/**
 * [MovieReviewViewModelImpl] unit test class.
 */
class MovieReviewViewModelImplTest {

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
    private lateinit var viewModel: MovieReviewViewModelImpl

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
        savedStateHandle.set(MovieReviewViewModelImpl.KEY_MOVIE_ID,movieId)

        // Initialize
        viewModel = MovieReviewViewModelImpl(model,savedStateHandle)
    }

    @Test(expected = IllegalArgumentException::class)
    fun throwExceptionWhenCreatedWithoutMovieIdState() {
        // Given a non initialized view model

        // When view model is initialized without review id value in saved state handle param
        viewModel = MovieReviewViewModelImpl(mockk(), SavedStateHandle())

        // Then view model should throw an IllegalArgumentException
    }

    @Test(expected = IllegalArgumentException::class)
    fun throwExceptionWhenCreatedWithMovieIdNullState() {
        // Given a non initialized view model

        // When view model is initialized with review id null value in saved state handle param
        val savedState = SavedStateHandle().apply {
            set(MovieReviewViewModelImpl.KEY_MOVIE_ID,null)
        }
        viewModel = MovieReviewViewModelImpl(mockk(),savedState)

        // Then view model should throw an IllegalArgumentException
    }

    @Test
    fun requestReviewFromModelAndUpdateViewStateWhenCreated() {
        // Given an initialized view model

        // Then view model should request an observable movie review from model with movie id state
        verify { model.execute(ReviewModelRequest(movieId)) }

        // When observable emit data
        val review = mockk<MovieReview>()
        modelReview.onNext(Result.Success(review))

        // Then view model should update view review state
        assertThat(viewModel.movieReview.value!!.data).isEqualTo(review)
    }

    @Test
    fun addModelReviewRxSubscriptionToContainer() {
        // Given an initialized

        // Then view model should add model review subscription to disposable container
        val field = RxViewModel::class.java.getDeclaredField("container")
        field.isAccessible = true
        val disposable = field.get(viewModel) as CompositeDisposable
        assertThat(disposable.size()).isEqualTo(1)
    }

    @Test
    fun updateViewErrorStateWhenModelReviewRequestFail() {
        // Given an initialized view model

        // Then view model should request an observable movie review from model with movie id state
        verify { model.execute(ReviewModelRequest(movieId)) }

        // When model returned observable emit an error result
        val appError = AppError("message",true)
        modelReview.onNext(Result.Error(appError))

        // Then view model should update review view state with error data
        assertThat(viewModel.movieReview.value).isInstanceOf(ViewData.Error::class.java)
        val viewError = viewModel.movieReview.value as ViewData.Error<MovieReview>
        assertThat(viewError.error.reason).isEqualTo(appError.cause)
        assertThat(viewError.error).isInstanceOf(ViewDataError.Retriable::class.java)
    }
}