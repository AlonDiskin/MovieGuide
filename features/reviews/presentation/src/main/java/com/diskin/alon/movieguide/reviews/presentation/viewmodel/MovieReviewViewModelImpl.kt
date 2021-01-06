package com.diskin.alon.movieguide.reviews.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.presentation.*
import com.diskin.alon.movieguide.reviews.presentation.data.FavoriteMovieModelRequest
import com.diskin.alon.movieguide.reviews.presentation.data.MovieReview
import com.diskin.alon.movieguide.reviews.presentation.data.ReviewModelRequest
import com.diskin.alon.movieguide.reviews.presentation.data.UnFavoriteMovieModelRequest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

/**
 * Stores and manage UI related data for the movie review UI controller.
 */
class MovieReviewViewModelImpl(
    private val model: Model,
    private val savedState: SavedStateHandle
) : RxViewModel(), MovieReviewViewModel{

    companion object { const val KEY_MOVIE_ID = "movie_id" }

    private val movieId = getMovieId()
    private val idSubject = BehaviorSubject.createDefault(movieId)
    private val favoritingSubject =  BehaviorSubject.create<Pair<String,Boolean>>()
    private val _modelReview = MutableLiveData<MovieReview>()
    override val movieReview: LiveData<MovieReview> get() = _modelReview
    private val _reviewUpdate = MutableLiveData<UpdateViewData>(UpdateViewData.Update)
    override val reviewUpdate: LiveData<UpdateViewData> get() = _reviewUpdate
    private val _reviewError =  MutableLiveData<ErrorViewData>()
    override val reviewError: LiveData<ErrorViewData> get() = _reviewError

    init {
        addSubscription(createReviewSubscription())
        addSubscription(createFavoritingSubscription())
    }

    override fun unFavoriteReviewedMovie() {
        _reviewUpdate.value = UpdateViewData.Update
        favoritingSubject.onNext(Pair(movieId,false))
    }

    override fun favoriteReviewedMovie() {
        _reviewUpdate.value = UpdateViewData.Update
        favoritingSubject.onNext(Pair(movieId,true))
    }

    private fun createFavoritingSubscription(): Disposable {
        return favoritingSubject
            .switchMapSingle { pair ->
                val request = when(pair.second) {
                    true -> FavoriteMovieModelRequest(pair.first)
                    false -> UnFavoriteMovieModelRequest(pair.first)
                }

                model.execute(request)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::handleModelFavoritingResult)
    }

    private fun handleModelFavoritingResult(result: Result<Unit>) {
        _reviewUpdate.value = UpdateViewData.EndUpdate
        _reviewError.value = ErrorViewData.NoError

        when(result) {
            is Result.Error -> handleFailedFavoritingResult(result)
        }
    }

    private fun handleFailedFavoritingResult(result: Result.Error<Unit>) {
        _reviewError.value = when(result.error.retriable) {
            true -> ErrorViewData.Retriable(result.error.description,::retryModelFavoriting)
            false -> ErrorViewData.NotRetriable(result.error.description)
        }
    }

    private fun retryModelFavoriting() {
        favoritingSubject.value?.let {
            _reviewUpdate.value = UpdateViewData.Update
            favoritingSubject.onNext(it)
        }
    }

    private fun createReviewSubscription(): Disposable {
        return idSubject
            .switchMap { id ->  model.execute(ReviewModelRequest(id)) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleModelReviewResult)
    }

    private fun handleModelReviewResult(result: Result<MovieReview>) {
        _reviewUpdate.value = UpdateViewData.EndUpdate
        _reviewError.value = ErrorViewData.NoError

        when(result) {
            is Result.Success -> handleSuccessfulReviewResult(result)
            is Result.Error -> handleFailedReviewResult(result)
        }
    }

    private fun handleSuccessfulReviewResult(result: Result.Success<MovieReview>) {
        _modelReview.value = result.data
    }

    private fun handleFailedReviewResult(result: Result.Error<MovieReview>) {
        _reviewError.value = when(result.error.retriable) {
            true -> ErrorViewData.Retriable(result.error.description,::retryModelReviewUpdate)
            false -> ErrorViewData.NotRetriable(result.error.description)
        }
    }

    private fun retryModelReviewUpdate() {
        _reviewUpdate.value = UpdateViewData.Update
        idSubject.onNext(movieId)
    }

    private fun getMovieId(): String {
        return savedState.get<String>(KEY_MOVIE_ID) ?:
        throw IllegalArgumentException("must contain movie id arg in stateHandle!")
    }
}
