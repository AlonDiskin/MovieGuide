package com.diskin.alon.movieguide.reviews.data.remote

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.appservices.toSingleResult
import com.diskin.alon.movieguide.common.util.Mapper2
import com.diskin.alon.movieguide.reviews.data.BuildConfig
import com.diskin.alon.movieguide.reviews.data.remote.data.MovieDetailResponse
import com.diskin.alon.movieguide.reviews.data.remote.data.TrailersResponse
import com.diskin.alon.movieguide.reviews.domain.entities.MovieReviewEntity
import com.diskin.alonmovieguide.common.data.NetworkErrorHandler
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handle remote api operations for [MovieReviewEntity] models.
 */
@Singleton
class MovieReviewStoreImpl @Inject constructor(
    private val api: TheMovieDbApi,
    private val mapper: Mapper2<MovieDetailResponse, TrailersResponse, MovieReviewEntity>,
    private val errorHandler: NetworkErrorHandler
) : MovieReviewStore {

    override fun getReview(id: String): Single<Result<MovieReviewEntity>> {
        return Observable.combineLatest(
            api.getMovieDetail(id.toInt(),BuildConfig.MOVIE_DB_API_KEY).toObservable(),
            api.getTrailers(id.toInt(),BuildConfig.MOVIE_DB_API_KEY).toObservable(),
            mapper::map
        )
            .subscribeOn(Schedulers.io())
            .firstOrError()
            .toSingleResult(errorHandler::handle)
    }
}
