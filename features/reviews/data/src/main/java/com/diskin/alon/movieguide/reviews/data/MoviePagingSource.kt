package com.diskin.alon.movieguide.reviews.data

import androidx.paging.rxjava2.RxPagingSource
import com.diskin.alon.movieguide.common.common.Mapper
import com.diskin.alon.movieguide.reviews.appservices.model.MovieSorting
import com.diskin.alon.movieguide.reviews.domain.MovieEntity
import com.diskin.alon.movieguide.reviews.data.MoviesResponse.MovieResponse
import com.diskin.alonmovieguide.common.data.NetworkErrorHandler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MoviePagingSource @Inject constructor(
    private val api: TheMovieDbApi,
    private val networkErrorHandler: NetworkErrorHandler,
    private val sorting: MovieSorting,
    private val mapper: Mapper<List<MovieResponse>,List<MovieEntity>>
) : RxPagingSource<String,MovieEntity>() {

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
            MovieSorting.RELEASE_DATE -> api.getByReleaseDate(1)
            MovieSorting.POPULARITY -> api.getByPopularity(1)
            MovieSorting.RATING -> api.getByRating(1)
        }
    }

    private fun loadPage(params: LoadParams<String>): Single<MoviesResponse> {
        // key should be passed as non null by paging api
        val page = params.key!!.toInt()

        return when(sorting) {
            MovieSorting.RELEASE_DATE -> api.getByReleaseDate(page)
            MovieSorting.POPULARITY -> api.getByPopularity(page)
            MovieSorting.RATING -> api.getByRating(page)
        }
    }

    private fun toLoadResult(response: MoviesResponse): LoadResult<String,MovieEntity> {
        // Set next key based on prev and max  pages in api
        val nextKey = if (response.page < response.total_pages) {
            (response.page + 1).toString()
        } else{
            null
        }

        // Map api response
        return LoadResult.Page(
            mapper.map(response.results),
            null,  // Only paging forward.
            nextKey,
            LoadResult.Page.COUNT_UNDEFINED,
            LoadResult.Page.COUNT_UNDEFINED
        )
    }

    private fun toLoadResultError(e: Throwable): LoadResult<String,MovieEntity> {
        val networkError = networkErrorHandler.handle(e)
        val throwable = Throwable(networkError.cause)

        return LoadResult.Error(throwable)
    }
}