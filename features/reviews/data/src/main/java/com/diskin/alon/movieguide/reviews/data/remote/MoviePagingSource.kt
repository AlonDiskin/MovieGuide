package com.diskin.alon.movieguide.reviews.data.remote

import androidx.paging.rxjava2.RxPagingSource
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.reviews.data.remote.data.MoviesResponse
import com.diskin.alon.movieguide.reviews.data.remote.data.MoviesResponse.MovieResponse
import com.diskin.alon.movieguide.reviews.data.remote.data.RemoteMovieSorting
import com.diskin.alon.movieguide.reviews.domain.entities.MovieEntity
import com.diskin.alonmovieguide.common.data.NetworkErrorHandler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MoviePagingSource @Inject constructor(
    private val api: TheMovieDbApi,
    private val networkErrorHandler: NetworkErrorHandler,
    private val sorting: RemoteMovieSorting,
    private val mapper: Mapper<MovieResponse, MovieEntity>
) : RxPagingSource<String, MovieEntity>() {

    override fun loadSingle(params: LoadParams<String>): Single<LoadResult<String, MovieEntity>> {
        val apiCall = when(params) {
            is LoadParams.Refresh -> loadRefresh()
            else -> loadPage(params)
        }

        return apiCall
            .subscribeOn(Schedulers.io())
            .map(this::toLoadResult)
            .onErrorReturn{ toLoadResultError(it) }
    }

    private fun loadRefresh(): Single<MoviesResponse> {
        return when(sorting) {
            RemoteMovieSorting.RELEASE_DATE -> api.getByReleaseDate(1)
            RemoteMovieSorting.POPULARITY -> api.getByPopularity(1)
            RemoteMovieSorting.RATING -> api.getByRating(1)
        }
    }

    private fun loadPage(params: LoadParams<String>): Single<MoviesResponse> {
        // key should be passed as non null by paging api
        val page = params.key!!.toInt()

        return when(sorting) {
            RemoteMovieSorting.RELEASE_DATE -> api.getByReleaseDate(page)
            RemoteMovieSorting.POPULARITY -> api.getByPopularity(page)
            RemoteMovieSorting.RATING -> api.getByRating(page)
        }
    }

    private fun toLoadResult(response: MoviesResponse): LoadResult<String, MovieEntity> {
        // Set next key based on prev and max  pages in api
        val nextKey = if (response.page < response.total_pages) {
            (response.page + 1).toString()
        } else{
            null
        }

        // Map api response
        return LoadResult.Page(
            response.results.map { mapper.map(it) },
            null,  // Only paging forward.
            nextKey,
            LoadResult.Page.COUNT_UNDEFINED,
            LoadResult.Page.COUNT_UNDEFINED
        )
    }

    private fun toLoadResultError(e: Throwable): LoadResult<String, MovieEntity> {
        val networkError = networkErrorHandler.handle(e)
        val throwable = Throwable(networkError.description)

        return LoadResult.Error(throwable)
    }
}