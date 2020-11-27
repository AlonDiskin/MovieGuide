package com.diskin.alon.movieguide.news.data.remote.util

import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.news.data.remote.data.FeedlyEntryResponse
import com.diskin.alon.movieguide.news.domain.ArticleEntity
import javax.inject.Inject

class ApiArticleMapper @Inject constructor() :
    Mapper<FeedlyEntryResponse, ArticleEntity> {

    override fun map(source: FeedlyEntryResponse): ArticleEntity {
        return ArticleEntity(
            source.id,
            source.title,
            source.summary.content,
            source.author,
            source.published,
            source.visual?.url ?: "",
            source.originId
        )
    }
}