package com.diskin.alon.movieguide.reviews.data.remote

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.diskin.alon.movieguide.reviews.appservices.data.MovieSorting
import com.diskin.alon.movieguide.reviews.domain.entities.MovieEntity
import com.diskin.alon.movieguide.common.appservices.Result
import io.reactivex.Observable
import io.reactivex.Single

/**
 * Remote source contract for [MovieEntity].
 */
interface MovieStore {

    fun getAllBySorting(config: PagingConfig, sorting: MovieSorting): Observable<PagingData<MovieEntity>>

    fun get(id: String): Single<Result<MovieEntity>>
}