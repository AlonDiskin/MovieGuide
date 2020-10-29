package com.diskin.alon.movieguide.news.presentation

import com.diskin.alon.movieguide.news.presentation.model.Article
import com.diskin.alon.movieguide.news.presentation.model.NewsHeadline

fun createNewsHeadlines(): List<NewsHeadline> {
    return listOf(
        NewsHeadline(
            "id1",
            "title1",
            "date1",
            "url1",
            "article1"
        ),
        NewsHeadline(
            "id2",
            "title2",
            "date2",
            "url2",
            "article2"
        ),
        NewsHeadline(
            "id3",
            "title3",
            "date3",
            "url3",
            "article3"
        )
    )
}

fun createTestArticle() =
    Article(
        "url",
        "title",
        "author",
        "content",
        "date",
        "image_url"
    )