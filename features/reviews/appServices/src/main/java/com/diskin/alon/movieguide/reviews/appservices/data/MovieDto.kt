package com.diskin.alon.movieguide.reviews.appservices.data

data class MovieDto(val id: String,
                    val title: String,
                    val popularity: Double,
                    val rating: Double,
                    val releaseDate: Long,
                    val posterUrl: String)