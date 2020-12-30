package com.diskin.alon.movieguide.reviews.presentation.data

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.presentation.ModelRequest
import com.diskin.alon.movieguide.reviews.appservices.data.AddFavoriteMovieRequest
import com.diskin.alon.movieguide.reviews.appservices.data.RemoveFavoriteMovieRequest
import io.reactivex.Single

data class UnFavoriteMovieModelRequest(
    val movieId: String
) : ModelRequest<RemoveFavoriteMovieRequest,Single<Result<Unit>>>(RemoveFavoriteMovieRequest(movieId))