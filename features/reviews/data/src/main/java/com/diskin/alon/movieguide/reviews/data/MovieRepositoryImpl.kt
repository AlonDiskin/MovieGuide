package com.diskin.alon.movieguide.reviews.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.rxjava2.observable
import com.diskin.alon.movieguide.common.common.Mapper
import com.diskin.alon.movieguide.reviews.appservices.interfaces.MovieRepository
import com.diskin.alon.movieguide.reviews.appservices.model.MovieSorting
import com.diskin.alon.movieguide.reviews.domain.MovieEntity
import com.diskin.alonmovieguide.common.data.NetworkErrorHandler
import javax.inject.Inject

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