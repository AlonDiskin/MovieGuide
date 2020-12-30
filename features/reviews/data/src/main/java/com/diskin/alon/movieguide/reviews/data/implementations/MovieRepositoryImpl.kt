package com.diskin.alon.movieguide.reviews.data.implementations

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.appservices.toSingleData
import com.diskin.alon.movieguide.reviews.appservices.data.MovieSorting
import com.diskin.alon.movieguide.reviews.appservices.interfaces.MovieRepository
import com.diskin.alon.movieguide.reviews.data.local.FavoriteMoviesStore
import com.diskin.alon.movieguide.reviews.data.remote.MovieStore
import com.diskin.alon.movieguide.reviews.domain.entities.MovieEntity
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles data sources operations to provide [MovieEntity]s.
 */
@Singleton
class MovieRepositoryImpl @Inject constructor(
    private val movieStore: MovieStore,
    private val favoriteStore: FavoriteMoviesStore
) : MovieRepository {

    override fun getAllBySorting(
        config: PagingConfig,
        sorting: MovieSorting
    ): Observable<PagingData<MovieEntity>> {
        return movieStore.getAllBySorting(config, sorting)
    }

    override fun addToFavorites(movieId: String): Single<Result<Unit>> {
        return movieStore.get(movieId)
            .toSingleData()
            .flatMap(favoriteStore::add)
    }

    override fun removeFromFavorites(movieId: String): Single<Result<Unit>> {
        return favoriteStore.remove(movieId)
    }

    override fun isFavorite(movieId: String): Observable<Result<Boolean>> {
        return favoriteStore.contains(movieId)
    }
}