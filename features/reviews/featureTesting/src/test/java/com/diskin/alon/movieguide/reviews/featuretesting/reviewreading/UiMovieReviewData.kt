package com.diskin.alon.movieguide.reviews.featuretesting.reviewreading

data class UiMovieReviewData(val title: String,
                             val rating: String,
                             val genres: String,
                             val releaseDate: String,
                             val summary: String,
                             val review: String,
                             val backDropImageUrl: String,
                             val trailersThumbnailUrls: List<String>)