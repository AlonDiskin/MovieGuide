package com.diskin.alon.movieguide.reviews.data.remote

import com.diskin.alon.movieguide.reviews.data.BuildConfig

const val MOVIE_DB_BASE_POSTER_PATH = "http://image.tmdb.org/t/p/w342"
const val MOVIE_DB_BASE_BACKDROP_PATH = "http://image.tmdb.org/t/p/w342"
const val YOUTUBE_THUMBNAIL_PATH = "https://img.youtube.com/vi/"
const val YOUTUBE_THUMBNAIL_PATH_END = "/0.jpg"
const val MOVIE_DB_MOVIES_PATH = "3/discover/movie"
const val MOVIE_DB_MOVIE_DETAIL_PATH = "3/movie"

const val MOVIE_DB_API_KEY_PARAM = "api_key"
const val MOVIE_DB_POP_MOVIES_PARAMS = "${MOVIE_DB_API_KEY_PARAM}=${BuildConfig.MOVIE_DB_API_KEY}&language=en&sort_by=popularity.desc&include_adult=false&include_video=false"
const val MOVIE_DB_RATING_MOVIES_PARAMS = "${MOVIE_DB_API_KEY_PARAM}=${BuildConfig.MOVIE_DB_API_KEY}&vote_count.gte=500&language=en&sort_by=vote_average.desc&include_adult=false&include_video=false"
const val MOVIE_DB_RELEASE_DATE_MOVIES_PARAMS = "${MOVIE_DB_API_KEY_PARAM}=${BuildConfig.MOVIE_DB_API_KEY}&language=en&sort_by=release_date.desc&include_adult=false&include_video=false"

const val MOVIE_DB_PARAM_PAGE = "page"
