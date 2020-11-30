package com.diskin.alon.movieguide.news.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.diskin.alon.movieguide.common.appservices.AppError
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.presentation.ErrorViewData
import com.diskin.alon.movieguide.common.presentation.Model
import com.diskin.alon.movieguide.common.presentation.RxViewModel
import com.diskin.alon.movieguide.common.presentation.UpdateViewData
import com.diskin.alon.movieguide.news.appservices.data.BookmarkSorting
import com.diskin.alon.movieguide.news.presentation.data.BookmarksModelRequest
import com.diskin.alon.movieguide.news.presentation.data.Headline
import com.diskin.alon.movieguide.news.presentation.data.UnBookmarkingModelRequest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

/**
 * Stores and manage UI related data for the bookmarks UI controller.
 */
class BookmarksViewModelImpl(
    private val model: Model,
    private val savedState: SavedStateHandle
) : RxViewModel(), BookmarksViewModel{

    companion object {
        private const val KEY_SORTING = "sorting"
        private val DEFAULT_SORTING = BookmarkSorting.NEWEST
    }

    private val removeBookmarksSubject =  BehaviorSubject.create<List<String>>()
    private val selectedSorting = BehaviorSubject.createDefault(defaultSelectedSorting())
    private val _bookmarks =  MutableLiveData<List<Headline>>()
    override val bookmarks: LiveData<List<Headline>> get() = _bookmarks
    private val _sorting = MutableLiveData<BookmarkSorting>()
    override val sorting: LiveData<BookmarkSorting> get() = _sorting
    private val _error = MutableLiveData<ErrorViewData>()
    override val error: LiveData<ErrorViewData> get() = _error
    private val _update = MutableLiveData<UpdateViewData>(UpdateViewData.Update)
    override val update: LiveData<UpdateViewData> get() = _update

    init {
        addSubscription(createBookmarksSubscription())
        addSubscription(createUnBookmarkingSubscription())
    }

    override fun sortBookmarks(sorting: BookmarkSorting) {
        savedState[KEY_SORTING] = sorting
        selectedSorting.onNext(sorting)
    }

    override fun removeBookmarks(ids: List<String>) {
        _update.value = UpdateViewData.Update
        removeBookmarksSubject.onNext(ids)
    }

    private fun defaultSelectedSorting() = savedState.get<BookmarkSorting>(KEY_SORTING) ?: DEFAULT_SORTING

    private fun createUnBookmarkingSubscription(): Disposable {
        return removeBookmarksSubject
            .flatMapSingle { ids -> model.execute(UnBookmarkingModelRequest(ids)) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::handleUnBookmarkingModelResult)
    }

    private fun handleUnBookmarkingModelResult(result : Result<Unit>) {
        when(result) {
            is Result.Success -> handleUnBookmarkingSuccess()
            is Result.Error -> handleUnBookmarkingError(result.error)
        }
    }

    private fun handleUnBookmarkingSuccess() {
        _update.value = UpdateViewData.EndUpdate
        _error.value = ErrorViewData.NoError
    }

    private fun handleUnBookmarkingError(error: AppError) {
        _error.value = when(error.retriable) {
            true -> ErrorViewData.Retriable(error.description,::retryUnBookmarking)
            false -> ErrorViewData.NotRetriable(error.description)
        }
    }

    private fun retryUnBookmarking() {
        removeBookmarksSubject.value?.let { removeBookmarksSubject.onNext(it) }
    }

    private fun createBookmarksSubscription() = selectedSorting
        .switchMap { model.execute(BookmarksModelRequest(it)) }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { handleBookmarksModelResult(it)}

    private fun handleBookmarksModelResult(result: Result<List<Headline>>) {
        when(result) {
            is Result.Success -> handleBookmarksModelUpdate(result.data)
            is Result.Error -> handleBookmarksModelError(result.error)
        }
    }

    private fun handleBookmarksModelUpdate(update: List<Headline>) {
        _bookmarks.value = update
        _sorting.value = selectedSorting.value
        _update.value = UpdateViewData.EndUpdate
        _error.value = ErrorViewData.NoError
    }

    private fun handleBookmarksModelError(error: AppError) {
        _error.value = when(error.retriable) {
            true -> ErrorViewData.Retriable(error.description,::retryBookmarksUpdate)
            false -> ErrorViewData.NotRetriable(error.description)
        }
    }

    private fun retryBookmarksUpdate() {
        selectedSorting.value?.let { selectedSorting.onNext(it) }
    }
}