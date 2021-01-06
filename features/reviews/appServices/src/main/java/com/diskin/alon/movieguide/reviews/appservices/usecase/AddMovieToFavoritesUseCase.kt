package com.diskin.alon.movieguide.reviews.appservices.usecase

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.appservices.UseCase
import com.diskin.alon.movieguide.reviews.appservices.data.AddFavoriteMovieRequest
import com.diskin.alon.movieguide.reviews.appservices.interfaces.MovieRepository
import io.reactivex.Single
import javax.inject.Inject

/**
 * Coordinate app operations to add a movie to user favorite movies collection.
 */
class AddMovieToFavoritesUseCase @Inject constructor(
    private val repository: MovieRepository
) : UseCase<AddFavoriteMovieRequest,Single<Result<Unit>>>  {

    override fun execute(param: AddFavoriteMovieRequest): Single<Result<Unit>> {
        return repository.addToFavorites(param.movieId)
    }
}