package com.diskin.alon.movieguide.reviews.data

const val MOVIE_DB_BASE_POSTER_PATH = "http://image.tmdb.org/t/p/w342"
const val MAX_MOVIE_DB_API_PAGES = 500
const val MOVIE_DB_MOVIES_PATH = "3/discover/movie"
const val MOVIE_DB_POP_MOVIES_PARAMS = "${BuildConfig.MOVIE_DB_API_KEY}&language=en&sort_by=popularity.desc&include_adult=false&include_video=false"
const val MOVIE_DB_RATING_MOVIES_PARAMS = "${BuildConfig.MOVIE_DB_API_KEY}&vote_count.gte=500&language=en&sort_by=vote_average.desc&include_adult=false&include_video=false"
const val MOVIE_DB_RELEASE_DATE_MOVIES_PARAMS = "${BuildConfig.MOVIE_DB_API_KEY}&language=en&sort_by=release_date.desc&include_adult=false&include_video=false"
const val MOVIE_DB_PARAM_PAGE = "page"