package com.diskin.alon.movieguide.reviews.appservices.interfaces

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.reviews.appservices.data.MovieSorting
import com.diskin.alon.movieguide.reviews.domain.entities.MovieEntity
import io.reactivex.Observable
import io.reactivex.Single

/**
 * [MovieEntity] repository contract.
 */
interface MovieRepository {

    fun getAllBySorting(config: PagingConfig, sorting: MovieSorting): Observable<PagingData<MovieEntity>>

    fun addToFavorites(movieId: String): Single<Result<Unit>>

    fun removeFromFavorites(movieId: String): Single<Result<Unit>>

    fun isFavorite(movieId: String): Observable<Result<Boolean>>
}