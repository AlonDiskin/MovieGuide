package com.diskin.alon.movieguide.news.appservices.util

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.news.appservices.data.HeadlineDto
import com.diskin.alon.movieguide.news.domain.ArticleEntity
import javax.inject.Inject

/**
 * Maps domain headline data  models to app service data models.
 */
class HeadlinesMapper @Inject constructor() : Mapper<Result<List<ArticleEntity>>, Result<List<HeadlineDto>>> {
    override fun map(source: Result<List<ArticleEntity>>): Result<List<HeadlineDto>> {
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