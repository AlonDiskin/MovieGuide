package com.diskin.alon.movieguide.reviews.appservices.data

import androidx.paging.PagingConfig
import com.diskin.alon.movieguide.reviews.appservices.data.MovieSorting

data class SortedMoviesRequest(val config: PagingConfig,
                               val sorting: MovieSorting
)