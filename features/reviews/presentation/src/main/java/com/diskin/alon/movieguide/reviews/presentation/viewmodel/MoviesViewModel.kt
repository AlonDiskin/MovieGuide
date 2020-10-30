package com.diskin.alon.movieguide.reviews.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.diskin.alon.movieguide.reviews.appservices.model.MovieSorting
import com.diskin.alon.movieguide.reviews.presentation.model.Movie

interface MoviesViewModel {

    val movies: LiveData<PagingData<Movie>>

    val sorting: LiveData<MovieSorting>

    fun sortMovies(sorting: MovieSorting)
}
