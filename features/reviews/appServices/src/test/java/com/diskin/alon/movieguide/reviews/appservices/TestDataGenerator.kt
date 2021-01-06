package com.diskin.alon.movieguide.reviews.appservices

import com.diskin.alon.movieguide.reviews.appservices.data.MovieReviewDto
import com.diskin.alon.movieguide.reviews.appservices.data.TrailerDto
import com.diskin.alon.movieguide.reviews.domain.entities.MovieReviewEntity

fun createMovieReviewEntity(): MovieReviewEntity {
    return MovieReviewEntity(
        "id",
        "title",
        6.7,
        1200L,
        "back_drop_poster_url",
        emptyList(),
        "summary",
        "review",
        "web_url"
    )
}

fun createMovieReviewDto(entity: MovieReviewEntity, favorite: Boolean): MovieReviewDto {
    return MovieReviewDto(
        entity.id,
        entity.title,
        entity.rating,
        entity.releaseDate,
        entity.backDropImageUrl,
        entity.genres.map { it.name },
        entity.summary,
        entity.review,
        entity.webUrl,
        entity.trailersUrl.map {
            TrailerDto(it.url,it.thumbnailUrl)
        },
        favorite
    )
}