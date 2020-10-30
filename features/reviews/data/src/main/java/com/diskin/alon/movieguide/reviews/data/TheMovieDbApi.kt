package com.diskin.alon.movieguide.reviews.data

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

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
}