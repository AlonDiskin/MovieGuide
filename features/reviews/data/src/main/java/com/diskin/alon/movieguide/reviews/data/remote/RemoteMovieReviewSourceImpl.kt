package com.diskin.alon.movieguide.reviews.data.remote

import com.diskin.alon.movieguide.common.appservices.Result
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

/**
 * Handle remote api operations for [MovieReviewEntity] models.
 */
class RemoteMovieReviewSourceImpl @Inject constructor(
    private val api: TheMovieDbApi,
    private val mapper: Mapper2<MovieDetailResponse, TrailersResponse, Result<MovieReviewEntity>>,
    private val errorHandler: NetworkErrorHandler
) : RemoteMovieReviewSource {

    override fun getReview(id: String): Single<Result<MovieReviewEntity>> {
        return Observable.combineLatest(
            api.getMovieDetail(id.toInt(),BuildConfig.MOVIE_DB_API_KEY).toObservable(),
            api.getTrailers(id.toInt(),BuildConfig.MOVIE_DB_API_KEY).toObservable(),
            mapper::map
        )
            .subscribeOn(Schedulers.io())
            .onErrorReturn { Result.Error(errorHandler.handle(it)) }
            .firstOrError()
    }
}
