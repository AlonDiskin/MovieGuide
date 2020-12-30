package com.diskin.alon.movieguide.reviews.appservices.usecase

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.appservices.UseCase
import com.diskin.alon.movieguide.reviews.appservices.data.RemoveFavoriteMovieRequest
import com.diskin.alon.movieguide.reviews.appservices.interfaces.MovieRepository
import io.reactivex.Single
import javax.inject.Inject

/**
 * Coordinate app operations to remove a movie from user favorite movies collection.
 */
class RemoveMovieFromFavoritesUseCase @Inject constructor(
    private val repository: MovieRepository
) : UseCase<RemoveFavoriteMovieRequest,Single<Result<Unit>>> {

    override fun execute(param: RemoveFavoriteMovieRequest): Single<Result<Unit>> {
        return repository.removeFromFavorites(param.movieId)
    }
}