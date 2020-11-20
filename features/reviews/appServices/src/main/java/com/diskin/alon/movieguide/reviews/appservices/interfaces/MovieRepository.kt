package com.diskin.alon.movieguide.reviews.appservices.interfaces

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.diskin.alon.movieguide.reviews.appservices.data.MovieSorting
import com.diskin.alon.movieguide.reviews.domain.entities.MovieEntity
import io.reactivex.Observable

/**
 * [MovieEntity] repository contract.
 */
interface MovieRepository {

    /**
     * Get an observable [PagingData] of [MovieEntity] sorted by [MovieSorting],
     * in desc order.
     */
    fun getAllBySorting(config: PagingConfig, sorting: MovieSorting): Observable<PagingData<MovieEntity>>
}