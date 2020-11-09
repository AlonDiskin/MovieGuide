package com.diskin.alon.movieguide.reviews.data.remote

import com.diskin.alon.movieguide.reviews.data.remote.data.MovieDetailResponse
import com.diskin.alon.movieguide.reviews.data.remote.data.MoviesResponse
import com.diskin.alon.movieguide.reviews.data.remote.data.TrailersResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * [https://api.themoviedb.org/] retrofit REST client api.
 */
interface TheMovieDbApi {

    @GET("$MOVIE_DB_MOVIES_PATH?$MOVIE_DB_POP_MOVIES_PARAMS")
    fun getByPopularity(
        @Query(MOVIE_DB_PARAM_PAGE) page: Int
    ): Single<MoviesResponse>

    @GET("$MOVIE_DB_MOVIES_PATH?$MOVIE_DB_RATING_MOVIES_PARAMS")
    fun getByRating(
        @Query(MOVIE_DB_PARAM_PAGE) page: Int
    ): Single<MoviesResponse>

    @GET("$MOVIE_DB_MOVIES_PATH?$MOVIE_DB_RELEASE_DATE_MOVIES_PARAMS")
    fun getByReleaseDate(
        @Query(MOVIE_DB_PARAM_PAGE) page: Int
    ): Single<MoviesResponse>

    @GET("${MOVIE_DB_MOVIE_DETAIL_PATH}/{id}")
    fun getMovieDetail(@Path("id") id: Int,
                       @Query(MOVIE_DB_API_KEY_PARAM) apiKey: String
    ): Single<MovieDetailResponse>

    @GET("${MOVIE_DB_MOVIE_DETAIL_PATH}/{id}/videos")
    fun getTrailers(@Path("id") id: Int,
                    @Query(MOVIE_DB_API_KEY_PARAM) apiKey: String
    ): Single<TrailersResponse>
}