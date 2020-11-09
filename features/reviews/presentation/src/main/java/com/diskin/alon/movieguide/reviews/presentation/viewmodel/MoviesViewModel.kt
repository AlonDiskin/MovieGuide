package com.diskin.alon.movieguide.reviews.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.diskin.alon.movieguide.reviews.appservices.data.MovieSorting
import com.diskin.alon.movieguide.reviews.presentation.data.Movie

interface MoviesViewModel {

    val movies: LiveData<PagingData<Movie>>

    val sorting: LiveData<MovieSorting>

    fun sortMovies(sorting: MovieSorting)
}
