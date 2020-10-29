package com.diskin.alon.movieguide.news.data.remote

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.common.Mapper
import com.diskin.alon.movieguide.news.domain.ArticleEntity
import javax.inject.Inject

class ApiArticleMapper @Inject constructor() :
    Mapper<List<FeedlyEntryResponse>, Result<ArticleEntity>> {

    override fun map(source: List<FeedlyEntryResponse>): Result<ArticleEntity> {
        val entry = source.first()
        val entity = ArticleEntity(
            entry.id,
            entry.title,
            entry.summary.content,
            entry.author,
            entry.published,
            entry.visual.url,
            entry.originId
        )

        return Result.Success(entity)
    }
}