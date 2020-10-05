package com.diskin.alon.movieguide.news.appservices

import com.diskin.alon.movieguide.news.domain.ArticleEntity
import com.diskin.alon.movieguide.news.domain.HeadlineEntity
import java.util.*

fun createHeadlines(): List<HeadlineEntity> {
    return listOf(
        HeadlineEntity(
            "id1",
            "title1",
            Calendar.getInstance(),
            "url1",
            "article1"
        ),
        HeadlineEntity(
            "id2",
            "title2",
            Calendar.getInstance(),
            "url2",
            "article2"
        ),
        HeadlineEntity(
            "id3",
            "title3",
            Calendar.getInstance(),
            "url3",
            "article3"
        )
    )
}

fun createTestArticleEntity() =
    ArticleEntity(
        "id",
        "title",
        "content",
        "author",
        Calendar.getInstance(),
        "image_url",
        "article_url"
    )