package com.diskin.alon.movieguide.reviews.data.implementations

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.rxjava2.observable
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.reviews.appservices.interfaces.MovieRepository
import com.diskin.alon.movieguide.reviews.appservices.data.MovieSorting
import com.diskin.alon.movieguide.reviews.data.remote.MoviePagingSource
import com.diskin.alon.movieguide.reviews.data.remote.data.MoviesResponse
import com.diskin.alon.movieguide.reviews.data.remote.TheMovieDbApi
import com.diskin.alon.movieguide.reviews.domain.entities.MovieEntity
import com.diskin.alonmovieguide.common.data.NetworkErrorHandler
import javax.inject.Inject

/**
 * Handles data sources operations to provide [MovieEntity]s.
 */
class MovieRepositoryImpl @Inject constructor(
    private val api: TheMovieDbApi,
    private val networkErrorHandler: NetworkErrorHandler,
    private val mapper: Mapper<List<MoviesResponse.MovieResponse>, List<MovieEntity>>
) : MovieRepository {

    override fun getAllBySorting(config: PagingConfig, sorting: MovieSorting) =
        Pager(config)
        { MoviePagingSource(
            api,
            networkErrorHandler,
            sorting,
            mapper
        ) }
        .observable
}