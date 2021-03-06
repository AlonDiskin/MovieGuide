package com.diskin.alon.movieguide.reviews.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.rxjava2.cachedIn
import com.diskin.alon.movieguide.common.presentation.Model
import com.diskin.alon.movieguide.common.presentation.RxViewModel
import com.diskin.alon.movieguide.reviews.presentation.data.Movie
import com.diskin.alon.movieguide.reviews.presentation.data.SearchMoviesModelRequest
import com.diskin.alon.movieguide.reviews.presentation.util.MoviesSearchModelDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

/**
 * Stores and manage UI related data for the movies search UI controller.
 */
@HiltViewModel
class MoviesSearchViewModel @Inject constructor(
    @MoviesSearchModelDispatcher private val model: Model,
    private val savedState: SavedStateHandle
) : RxViewModel() {

    companion object {
        const val RESULTS_PAGE_SIZE = 20
        const val SEARCH_TXT_KEY = "search_text"
        const val SEARCH_QUERY_KEY = "search_query"
        const val DEF_SEARCH_TXT = ""
    }

    private val querySubject = BehaviorSubject.create<String>()
    private val _results = MutableLiveData<PagingData<Movie>>()
    val results: LiveData<PagingData<Movie>> get() = _results
    var searchText: String = savedState[SEARCH_TXT_KEY] ?: DEF_SEARCH_TXT
    set(value) = savedState.set(SEARCH_TXT_KEY,value)

    init {
        addSubscription(createQuerySubscription())
        restoreLastSearchIfExist()
    }

    fun search(query: String) {
        storeQueryState(query)
        querySubject.onNext(query)
    }

    private fun createQuerySubscription(): Disposable {
        return querySubject
            .switchMap { model.execute(SearchMoviesModelRequest(it, PagingConfig(RESULTS_PAGE_SIZE)))}
            .cachedIn(viewModelScope)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { paging -> _results.value = paging }
    }

    private fun storeQueryState(query: String) {
        savedState.set(SEARCH_QUERY_KEY,query)
    }

    private fun restoreLastSearchIfExist() {
        savedState.get<String>(SEARCH_QUERY_KEY)?.let { querySubject.onNext(it) }
    }
}
