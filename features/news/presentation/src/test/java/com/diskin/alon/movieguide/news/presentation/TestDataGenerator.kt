package com.diskin.alon.movieguide.news.presentation

import com.diskin.alon.movieguide.news.presentation.data.Article
import com.diskin.alon.movieguide.news.presentation.data.Headline

fun createNewsHeadlines(): List<Headline> {
    return listOf(
        Headline(
            "id1",
            "title1",
            "date1",
            "url1",
            "article1"
        ),
        Headline(
            "id2",
            "title2",
            "date2",
            "url2",
            "article2"
        ),
        Headline(
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
        "image_url",
        true
    )

fun createUnBookmarkedTestArticle() =
    Article(
        "url",
        "title",
        "author",
        "content",
        "date",
        "image_url",
        false
    )

fun createBookmarkedTestArticle() =
    Article(
        "url",
        "title",
        "author",
        "content",
        "date",
        "image_url",
        true
    )