package com.diskin.alon.movieguide.news.appservices.util

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.news.appservices.data.HeadlineDto
import com.diskin.alon.movieguide.news.domain.HeadlineEntity

/**
 * Maps domain headline data  models to app service data models.
 */
class HeadlinesMapper : Mapper<Result<List<HeadlineEntity>>, Result<List<HeadlineDto>>> {
    override fun map(source: Result<List<HeadlineEntity>>): Result<List<HeadlineDto>> {
        return when(source) {
            is Result.Success -> {
                Result.Success(
                    source.data.map {
                        HeadlineDto(
                            it.id,
                            it.title,
                            it.date,
                            it.imageUrl,
                            it.articleUrl
                        )
                    }
                )
            }

            is Result.Error -> Result.Error(source.error)
        }
    }
}