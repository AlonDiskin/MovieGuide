package com.diskin.alon.movieguide.news.appservices.util

import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.news.appservices.data.HeadlineDto
import com.diskin.alon.movieguide.news.domain.ArticleEntity

/**
 * Maps domain headline data  models to app service data models.
 */
class HeadlinesDtoMapper : Mapper<List<ArticleEntity>, List<HeadlineDto>> {

    override fun map(source: List<ArticleEntity>): List<HeadlineDto> {
        return source.map {
            HeadlineDto(
                it.id,
                it.title,
                it.date,
                it.imageUrl,
                it.articleUrl
            )
        }
    }
}