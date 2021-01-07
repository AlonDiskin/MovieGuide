package com.diskin.alon.movieguide.reviews.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.diskin.alon.movieguide.reviews.presentation.data.Movie

interface MoviesSearchViewModel {

    val results: LiveData<PagingData<Movie>>

    var searchText: String

    fun search(query: String)
}
