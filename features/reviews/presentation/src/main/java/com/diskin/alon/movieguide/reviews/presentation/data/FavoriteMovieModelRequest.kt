package com.diskin.alon.movieguide.reviews.presentation.data

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.presentation.ModelRequest
import com.diskin.alon.movieguide.reviews.appservices.data.AddFavoriteMovieRequest
import io.reactivex.Single

data class FavoriteMovieModelRequest(
    val movieId: String
) : ModelRequest<AddFavoriteMovieRequest,Single<Result<Unit>>>(AddFavoriteMovieRequest(movieId))