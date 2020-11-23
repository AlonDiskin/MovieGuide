package com.diskin.alon.movieguide.reviews.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.presentation.Model
import com.diskin.alon.movieguide.common.presentation.RxViewModel
import com.diskin.alon.movieguide.common.presentation.ViewData
import com.diskin.alon.movieguide.common.presentation.ViewDataError
import com.diskin.alon.movieguide.reviews.presentation.data.MovieReview
import com.diskin.alon.movieguide.reviews.presentation.data.ReviewModelRequest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject

/**
 * Stores and manage UI related data for the movie review UI controller.
 */
class MovieReviewViewModelImpl(
    model: Model,
    savedStateHandle: SavedStateHandle
) : RxViewModel(), MovieReviewViewModel{

    companion object {
        const val KEY_MOVIE_ID = "movie_id"
    }

    private val idSubject = BehaviorSubject.create<String>()
    private val _modelReview = MutableLiveData<ViewData<MovieReview>>()
    override val movieReview: LiveData<ViewData<MovieReview>> get() = _modelReview

    init {
        // Get id from state handle
        val movieId = savedStateHandle.get<String>(KEY_MOVIE_ID) ?:
        throw IllegalArgumentException("must contain movie id arg in stateHandle!")

        // Create rx chain for review model subscription
        val reviewSubscription = idSubject
            .switchMap { id ->  model.execute(ReviewModelRequest(id)) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result ->
                    when(result) {
                        is Result.Success -> _modelReview.value = ViewData.Data(result.data)

                        is Result.Error -> _modelReview.value = ViewData.Error(
                            if (result.error.retriable) {
                                ViewDataError.Retriable(result.error.cause) {
                                    _modelReview.value = ViewData.Updating(_modelReview.value?.data)
                                    idSubject.onNext(movieId)
                                }
                            } else {
                                ViewDataError.NotRetriable(result.error.cause)
                            }
                        )
                    }
                }

        addSubscription(reviewSubscription)

        // Initiate review request from model
        idSubject.onNext(movieId)
    }
}
