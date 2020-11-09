package com.diskin.alon.movieguide.reviews.presentation.util

import androidx.paging.PagingData
import androidx.paging.map
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.reviews.appservices.data.MovieDto
import com.diskin.alon.movieguide.reviews.presentation.data.Movie
import javax.inject.Inject

class MoviePagingMapper @Inject constructor() : Mapper<PagingData<MovieDto>, PagingData<Movie>> {

    override fun map(source: PagingData<MovieDto>): PagingData<Movie> {
        return source.map {
            Movie(
                it.id,
                it.title,
                it.posterUrl,
                it.rating.toString()
            )
        }
    }
}