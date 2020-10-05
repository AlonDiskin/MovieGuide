package com.diskin.alon.movieguide.news.data.implementation

import com.diskin.alon.movieguide.news.data.remote.FeedlyEntryResponse
import com.diskin.alon.movieguide.news.domain.ArticleEntity
import java.util.*

fun mapApiEntryResponseToArticleEntity(entry: FeedlyEntryResponse): ArticleEntity {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = entry.published

    return ArticleEntity(
        entry.id,
        entry.title,
        entry.summary.content,
        entry.author,
        calendar,
        entry.visual.url,
        entry.originId
        )
}