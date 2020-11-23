package com.diskin.alon.movieguide.news.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.presentation.Model
import com.diskin.alon.movieguide.common.presentation.RxViewModel
import com.diskin.alon.movieguide.common.presentation.ViewData
import com.diskin.alon.movieguide.news.appservices.data.BookmarkSorting
import com.diskin.alon.movieguide.news.presentation.data.BookmarksModelRequest
import com.diskin.alon.movieguide.news.presentation.data.Headline
import io.reactivex.android.schedulers.AndroidSchedulers
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

    private val selectedSorting = BehaviorSubject.createDefault(defaultSelectedSorting())
    private val _bookmarks =  MutableLiveData<ViewData<List<Headline>>>(defaultBookmarksState())
    override val bookmarks: LiveData<ViewData<List<Headline>>> get() = _bookmarks
    private val _sorting = MutableLiveData<BookmarkSorting>()
    override val sorting: LiveData<BookmarkSorting> get() = _sorting

    init {
        addSubscription(createBookmarksSubscription())
    }

    override fun sortBookmarks(sorting: BookmarkSorting) {
        savedState[KEY_SORTING] = sorting
        selectedSorting.onNext(sorting)
    }

    private fun defaultSelectedSorting() = savedState.get<BookmarkSorting>(KEY_SORTING) ?: DEFAULT_SORTING

    private fun defaultBookmarksState() = ViewData.Updating<List<Headline>>()

    private fun createBookmarksSubscription() = selectedSorting
        .switchMap { model.execute(BookmarksModelRequest(it)) }
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe { handleBookmarksModelUpdate(it)}

    private fun handleBookmarksModelUpdate(result: Result<List<Headline>>) {
        when(result) {
            is Result.Success -> {
                _bookmarks.value = ViewData.Data(result.data)
                _sorting.value = selectedSorting.value
            }
        }
    }
}