package com.diskin.alon.movieguide.news.data

import com.diskin.alon.movieguide.news.data.local.data.Bookmark
import com.diskin.alon.movieguide.news.data.remote.data.FeedlyEntryResponse
import com.diskin.alon.movieguide.news.data.remote.data.FeedlyEntryResponse.Summary
import com.diskin.alon.movieguide.news.data.remote.data.FeedlyEntryResponse.Visual
import com.diskin.alon.movieguide.news.data.remote.data.FeedlyFeedResponse
import com.diskin.alon.movieguide.news.domain.ArticleEntity

fun createApiEntryResponse(): FeedlyEntryResponse {
    return FeedlyEntryResponse(
        "id",
        "title",
        "author",
        100L,
        Visual("url"),
        "originId",
        Summary("content")
    )
}

fun createFeedlyFeedResponse(): FeedlyFeedResponse {
    return FeedlyFeedResponse(
        listOf(
            FeedlyEntryResponse(
                "id1",
                "title1",
                "author1",
                100L,
                Visual("url1"),
                "originId1",
                Summary("content1")
            ),
            FeedlyEntryResponse(
                "id2",
                "title2",
                "author2",
                130L,
                Visual("url2"),
                "originId2",
                Summary("content2")
            )
        ),
        "continuation"
    )
}

fun createFeedlyFeedLastPageResponse(): FeedlyFeedResponse {
    return FeedlyFeedResponse(
        listOf(
            FeedlyEntryResponse(
                "id1",
                "title1",
                "author1",
                100L,
                Visual("url1"),
                "originId1",
                Summary("content1")
            ),
            FeedlyEntryResponse(
                "id2",
                "title2",
                "author2",
                130L,
                Visual("url2"),
                "originId2",
                Summary("content2")
            )
        ),
        null
    )
}

fun getArticleEntity(entry: FeedlyEntryResponse): ArticleEntity {
    return ArticleEntity(
        entry.id,
        entry.title,
        entry.summary.content,
        entry.author,
        entry.published,
        entry.visual?.url ?: "",
        entry.originId
    )
}

fun createIllegalPathId(): String {
    return "uM+MqpK9duOyb/imN0cFmOAhKFCAsXozhxb+qTAQU1w=_174af56171b:70b9d4:70edfa5f"
}

fun createLocalHeadlines(): Array<Bookmark> {
    return arrayOf(
        Bookmark("article_id_1"),
        Bookmark("article_id_2"),
        Bookmark("article_id_3")
    )
}