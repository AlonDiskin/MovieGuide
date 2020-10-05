package com.diskin.alon.movieguide.news.presentation.viewmodel

import androidx.paging.PagingData
import androidx.paging.map
import com.diskin.alon.movieguide.news.appservices.model.ArticleDto
import com.diskin.alon.movieguide.news.appservices.model.HeadlineDto
import com.diskin.alon.movieguide.news.presentation.model.Article
import com.diskin.alon.movieguide.news.presentation.model.NewsHeadline
import org.joda.time.LocalDateTime

/**
 * Utility functions that map presentation module models classes and any other dependent module
 * model classes.
 */

const val DATE_FORMAT = "dd MMM HH:mm"

/**
 * Maps [PagingData] of [HeadlineDto] to [PagingData] of [NewsHeadline].
 */
fun mapDtoPagingToNewsHeadline(data: PagingData<HeadlineDto>): PagingData<NewsHeadline> {
    return data.map { mapHeadlineDtoToNewsHeadline(it) }
}

/**
 * Map [HeadlineDto] to [NewsHeadline]
 */
fun mapHeadlineDtoToNewsHeadline(dto: HeadlineDto): NewsHeadline {
    return NewsHeadline(
        dto.id,
        dto.title,
        LocalDateTime(dto.date).toString(DATE_FORMAT),
        dto.imageUrl,
        dto.articleUrl
    )
}

fun mapArticleDto(dto: ArticleDto) =
    Article(
        dto.articleUrl
    )
