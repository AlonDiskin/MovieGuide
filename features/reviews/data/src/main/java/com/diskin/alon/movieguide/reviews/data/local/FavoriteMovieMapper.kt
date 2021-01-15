package com.diskin.alon.movieguide.reviews.data.local

import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.reviews.domain.entities.MovieEntity
import org.joda.time.LocalDate
import java.util.*
import javax.inject.Inject

class FavoriteMovieMapper @Inject constructor() : Mapper<MovieEntity, FavoriteMovie> {

    override fun map(source: MovieEntity): FavoriteMovie {
        return FavoriteMovie(
            source.id,
            source.title,
            source.popularity,
            source.rating,
            source.releaseDate ?: Calendar.getInstance().timeInMillis,
            source.posterUrl
        )
    }

    private fun mapReleaseDateDate(releaseDate: String?): Long? {
        return if (releaseDate != null && releaseDate.isNotEmpty()) {
            LocalDate.parse(releaseDate).toDate().time
        } else {
            null
        }
    }
}