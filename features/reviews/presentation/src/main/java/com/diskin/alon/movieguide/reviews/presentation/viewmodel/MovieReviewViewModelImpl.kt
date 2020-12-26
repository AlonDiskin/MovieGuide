package com.diskin.alon.movieguide.reviews.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.presentation.Model
import com.diskin.alon.movieguide.common.presentation.RxViewModel
import com.diskin.alon.movieguide.common.presentation.ViewData
import com.diskin.alon.movieguide.common.presentation.ErrorViewData
import com.diskin.alon.movieguide.reviews.presentation.data.MovieReview
import com.diskin.alon.movieguide.reviews.presentation.data.ReviewModelRequest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

/**
 * Stores and manage UI related data for the movie review UI controller.
 */
class MovieReviewViewModelImpl(
    private val model: Model,
    private val savedStateHandle: SavedStateHandle
) : RxViewModel(), MovieReviewViewModel{

    companion object {
        const val KEY_MOVIE_ID = "movie_id"
    }

    private val idSubject = BehaviorSubject.create<String>()
    private val movieId = getMovieId()
    private val _modelReview = MutableLiveData<ViewData<MovieReview>>()
    override val movieReview: LiveData<ViewData<MovieReview>> get() = _modelReview

    init {
        addSubscription(createReviewSubscription())
        idSubject.onNext(movieId)
    }

    private fun createReviewSubscription(): Disposable {
        return idSubject
            .switchMap { id ->  model.execute(ReviewModelRequest(id)) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleModelReviewResult)
    }

    private fun getMovieId(): String {
        return savedStateHandle.get<String>(KEY_MOVIE_ID) ?:
        throw IllegalArgumentException("must contain movie id arg in stateHandle!")
    }

    private fun handleModelReviewResult(result: Result<MovieReview>) {
        when(result) {
            is Result.Success -> handleSuccessfulReviewResult(result)
            is Result.Error -> handleFailedReviewResult(result)
        }
    }

    private fun handleSuccessfulReviewResult(result: Result.Success<MovieReview>) {
        _modelReview.value = ViewData.Data(result.data)
    }

    private fun handleFailedReviewResult(result: Result.Error<MovieReview>) {
        _modelReview.value = ViewData.Error(
            if (result.error.retriable) {
                val retryAction = {
                    _modelReview.value = ViewData.Updating(_modelReview.value?.data)
                    idSubject.onNext(movieId)
                }
                ErrorViewData.Retriable(result.error.description,retryAction)

            } else {
                ErrorViewData.NotRetriable(result.error.description)
            }
        )
    }
}
