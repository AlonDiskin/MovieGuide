package com.diskin.alon.movieguide.news.appservices

import com.diskin.alon.movieguide.common.appservices.AppError
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.news.appservices.data.ArticleDto
import com.diskin.alon.movieguide.news.domain.ArticleEntity
import com.diskin.alon.movieguide.news.domain.HeadlineEntity

fun createHeadlines(): List<HeadlineEntity> {
    return listOf(
        HeadlineEntity(
            "id1",
            "title1",
            100L,
            "url1",
            "article1"
        ),
        HeadlineEntity(
            "id2",
            "title2",
            100L,
            "url2",
            "article2"
        ),
        HeadlineEntity(
            "id3",
            "title3",
            100L,
            "url3",
            "article3"
        )
    )
}

fun createArticleEntity(): ArticleEntity {
    return ArticleEntity(
        "id",
        "title",
        "content",
        "author",
        100L,
        "image_url",
        "article_url"
    )
}

fun mapArticleDtoSuccessResult(entity: ArticleEntity): Result.Success<ArticleDto> {
    return Result.Success(
        ArticleDto(
            entity.title,
            entity.content,
            entity.author,
            entity.date,
            entity.imageUrl,
            entity.articleUrl
        )
    )
}

fun createAppError(): AppError {
    return AppError(
        "cause",
        true
    )
}

