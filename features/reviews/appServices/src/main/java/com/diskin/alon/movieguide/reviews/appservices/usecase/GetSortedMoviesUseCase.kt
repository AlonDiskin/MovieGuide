package com.diskin.alon.movieguide.reviews.appservices.usecase

import androidx.paging.PagingData
import com.diskin.alon.movieguide.common.appservices.UseCase
import com.diskin.alon.movieguide.common.common.Mapper
import com.diskin.alon.movieguide.reviews.appservices.interfaces.MovieRepository
import com.diskin.alon.movieguide.reviews.appservices.model.SortedMoviesRequest
import com.diskin.alon.movieguide.reviews.appservices.model.MovieDto
import com.diskin.alon.movieguide.reviews.domain.MovieEntity
import io.reactivex.Observable
import javax.inject.Inject

class GetSortedMoviesUseCase @Inject constructor(
    private val repository: MovieRepository,
    private val mapper: Mapper<PagingData<MovieEntity>,PagingData<MovieDto>>
) : UseCase<SortedMoviesRequest,Observable<PagingData<MovieDto>>> {

    override fun execute(param: SortedMoviesRequest): Observable<PagingData<MovieDto>> {
        return repository
            .getAllBySorting(param.config,param.sorting)
            .map(mapper::map)
    }
}