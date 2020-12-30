package com.diskin.alon.movieguide.reviews.data.remote

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.rxjava2.observable
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.appservices.toSingleResult
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.reviews.appservices.data.MovieSorting
import com.diskin.alon.movieguide.reviews.data.BuildConfig
import com.diskin.alon.movieguide.reviews.data.remote.data.MoviesResponse
import com.diskin.alon.movieguide.reviews.domain.entities.MovieEntity
import com.diskin.alonmovieguide.common.data.NetworkErrorHandler
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieStoreImpl @Inject constructor(
    private val api: TheMovieDbApi,
    private val networkErrorHandler: NetworkErrorHandler,
    private val mapper: Mapper<MoviesResponse.MovieResponse, MovieEntity>,
) : MovieStore {

    override fun getAllBySorting(
        config: PagingConfig,
        sorting: MovieSorting
    ): Observable<PagingData<MovieEntity>> {
        return Pager(config)
        { MoviePagingSource(
            api,
            networkErrorHandler,
            sorting,
            mapper
        ) }
            .observable
    }

    override fun get(id: String): Single<Result<MovieEntity>> {
        return api.getMovie(id.toInt(),BuildConfig.MOVIE_DB_API_KEY)
            .subscribeOn(Schedulers.io())
            .map(mapper::map)
            .toSingleResult(networkErrorHandler::handle)
    }
}