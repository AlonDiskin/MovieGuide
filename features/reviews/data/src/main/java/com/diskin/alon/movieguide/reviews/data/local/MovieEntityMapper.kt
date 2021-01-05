package com.diskin.alon.movieguide.reviews.data.local

import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.reviews.domain.entities.MovieEntity
import javax.inject.Inject

class MovieEntityMapper @Inject constructor() : Mapper<FavoriteMovie,MovieEntity> {
    override fun map(source: FavoriteMovie): MovieEntity {
        return MovieEntity(
            source.id,
            source.title,
            source.popularity,
            source.rating,
            source.releaseDate,
            source.posterUrl
        )
    }
}