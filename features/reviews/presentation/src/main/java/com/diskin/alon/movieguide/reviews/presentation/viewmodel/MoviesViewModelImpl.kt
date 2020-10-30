package com.diskin.alon.movieguide.reviews.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.rxjava2.cachedIn
import com.diskin.alon.movieguide.common.appservices.UseCase
import com.diskin.alon.movieguide.common.common.Mapper
import com.diskin.alon.movieguide.common.presentation.RxViewModel
import com.diskin.alon.movieguide.reviews.appservices.model.MovieDto
import com.diskin.alon.movieguide.reviews.appservices.model.MovieSorting
import com.diskin.alon.movieguide.reviews.appservices.model.SortedMoviesRequest
import com.diskin.alon.movieguide.reviews.presentation.model.Movie
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject

class MoviesViewModelImpl(
    private val getMoviesUseCase: UseCase<SortedMoviesRequest, Observable<PagingData<MovieDto>>>,
    private val pagingMapper: Mapper<PagingData<MovieDto>, PagingData<Movie>>,
    private val savedState: SavedStateHandle
) : MoviesViewModel, RxViewModel() {

    companion object {
        private const val MOVIES_PAGE_SIZE = 20
        private const val KEY_SORTING = "sorting"
        private val DEFAULT_SORTING = MovieSorting.POPULARITY
    }

    private val selectedSorting =
        BehaviorSubject.createDefault(getSavedSortingState())

    private val _movies = MutableLiveData<PagingData<Movie>>()
    override val movies: LiveData<PagingData<Movie>>
        get() = _movies

    private val _currentSorting = MutableLiveData<MovieSorting>()
    override val sorting: LiveData<MovieSorting>
        get() = _currentSorting

    init {
        // Subscribe to movies paging observable
        val subscription = selectedSorting
            .switchMap { sorting -> getMoviesUseCase.execute(
                SortedMoviesRequest(
                    PagingConfig(MOVIES_PAGE_SIZE),
                    sorting
                )
            )
            }
            .cachedIn(viewModelScope)
            .observeOn(AndroidSchedulers.mainThread())
            .map(pagingMapper::map)
            .subscribe { paging ->
                _currentSorting.value = selectedSorting.value
                _movies.value = paging
            }


        addSubscription(subscription)
    }

    override fun sortMovies(sorting: MovieSorting) {
        savedState.set(KEY_SORTING,sorting.name)
        selectedSorting.onNext(sorting)
    }

    private fun getSavedSortingState(): MovieSorting =
        MovieSorting.valueOf(savedState.get<String>(KEY_SORTING) ?: DEFAULT_SORTING.name)
}
