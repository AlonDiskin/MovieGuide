package com.diskin.alon.movieguide.reviews.appservices.data

import androidx.paging.PagingConfig

data class SearchMoviesRequest(val query: String,val config: PagingConfig)
