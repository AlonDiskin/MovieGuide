package com.diskin.alon.movieguide.reviews.presentation.util

import androidx.paging.PagingData
import androidx.paging.map
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.reviews.appservices.data.MovieDto
import com.diskin.alon.movieguide.reviews.presentation.data.Movie
import io.reactivex.Observable
import javax.inject.Inject

class MoviesPagingMapper @Inject constructor() : Mapper<Observable<PagingData<MovieDto>>, Observable<PagingData<Movie>>> {
    override fun map(source: Observable<PagingData<MovieDto>>): Observable<PagingData<Movie>> {
        return source.map { paging ->
            paging.map { dto ->
                Movie(
                    dto.id,
                    dto.title,
                    dto.posterUrl,
                    dto.rating.toString()
                )
            }
        }
    }
}