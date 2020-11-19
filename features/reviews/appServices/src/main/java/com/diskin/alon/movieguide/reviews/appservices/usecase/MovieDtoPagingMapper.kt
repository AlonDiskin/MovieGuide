package com.diskin.alon.movieguide.reviews.appservices.usecase

import androidx.paging.PagingData
import androidx.paging.map
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.reviews.appservices.data.MovieDto
import com.diskin.alon.movieguide.reviews.domain.entities.MovieEntity
import javax.inject.Inject

class MovieDtoPagingMapper @Inject constructor() : Mapper<PagingData<MovieEntity>, PagingData<MovieDto>> {

    override fun map(source: PagingData<MovieEntity>): PagingData<MovieDto> {
        return source.map {
            MovieDto(
                it.id,
                it.title,
                it.popularity,
                it.rating,
                it.releaseDate,
                it.posterUrl
            )
        }
    }
}