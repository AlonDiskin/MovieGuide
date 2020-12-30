package com.diskin.alon.movieguide.reviews.data.local

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.reviews.domain.entities.MovieEntity
import io.reactivex.Observable
import io.reactivex.Single

/**
 * Local source contract for favorite [MovieEntity].
 */
interface FavoriteMoviesStore {

    fun add(movie: MovieEntity): Single<Result<Unit>>

    fun remove(id: String): Single<Result<Unit>>

    fun contains(id: String): Observable<Result<Boolean>>
}