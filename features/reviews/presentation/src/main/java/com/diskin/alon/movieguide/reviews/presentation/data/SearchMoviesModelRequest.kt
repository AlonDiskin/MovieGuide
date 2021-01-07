package com.diskin.alon.movieguide.reviews.presentation.data

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.diskin.alon.movieguide.common.presentation.ModelRequest
import com.diskin.alon.movieguide.reviews.appservices.data.SearchMoviesRequest
import io.reactivex.Observable

data class SearchMoviesModelRequest(
    val query: String,
    val config: PagingConfig
) : ModelRequest<SearchMoviesRequest,Observable<PagingData<Movie>>>(SearchMoviesRequest(query,config))