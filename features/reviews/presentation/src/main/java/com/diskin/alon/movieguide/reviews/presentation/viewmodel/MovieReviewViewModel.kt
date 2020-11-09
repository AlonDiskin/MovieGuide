package com.diskin.alon.movieguide.reviews.presentation.viewmodel

import androidx.lifecycle.LiveData
import com.diskin.alon.movieguide.common.presentation.LoadState
import com.diskin.alon.movieguide.common.presentation.ViewData
import com.diskin.alon.movieguide.reviews.presentation.data.MovieReview

/**
 * Movie review view model contract.
 */
interface MovieReviewViewModel {

    val movieReview: LiveData<ViewData<MovieReview>>
}
