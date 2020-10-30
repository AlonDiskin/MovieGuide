package com.diskin.alon.movieguide.reviews.appservices.model

import androidx.paging.PagingConfig
import com.diskin.alon.movieguide.reviews.appservices.model.MovieSorting

data class SortedMoviesRequest(val config: PagingConfig,
                               val sorting: MovieSorting
)