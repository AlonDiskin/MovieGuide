package com.diskin.alon.movieguide.reviews.appservices.usecase

import androidx.paging.PagingData
import com.diskin.alon.movieguide.common.appservices.UseCase
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.reviews.appservices.data.MovieDto
import com.diskin.alon.movieguide.reviews.appservices.data.SearchMoviesRequest
import com.diskin.alon.movieguide.reviews.appservices.interfaces.MovieRepository
import com.diskin.alon.movieguide.reviews.domain.entities.MovieEntity
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Coordinate app operations to search for movies.
 */
class SearchMoviesUseCase @Inject constructor(
    private val repository: MovieRepository,
    private val mapper: Mapper<PagingData<MovieEntity>, PagingData<MovieDto>>
) : UseCase<SearchMoviesRequest,Observable<PagingData<MovieDto>>> {

    override fun execute(param: SearchMoviesRequest): Observable<PagingData<MovieDto>> {
        return repository.search(param.query,param.config).map(mapper::map)
    }
}
