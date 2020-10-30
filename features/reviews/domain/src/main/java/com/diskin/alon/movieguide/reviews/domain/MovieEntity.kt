package com.diskin.alon.movieguide.reviews.domain

data class MovieEntity(val id: String,
                       val title: String,
                       val popularity: Double,
                       val rating: Double,
                       val releaseDate: Long,
                       val posterUrl: String)