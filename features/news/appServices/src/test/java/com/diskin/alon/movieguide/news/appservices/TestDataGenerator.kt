package com.diskin.alon.movieguide.news.appservices

import com.diskin.alon.movieguide.common.appservices.AppError
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.news.appservices.data.ArticleDto
import com.diskin.alon.movieguide.news.domain.ArticleEntity

fun createHeadlines(): List<ArticleEntity> {
    return listOf(
        ArticleEntity(
            "id1",
            "title1",
            "content1",
            "author1",
            102345L,
            "imageUrl1",
            "articleUrl1"
        ),
        ArticleEntity(
            "id2",
            "title2",
            "content2",
            "author2",
            10234545L,
            "imageUrl2",
            "articleUrl2"
        ),
        ArticleEntity(
            "id3",
            "title3",
            "content3",
            "author3",
            10236745L,
            "imageUrl3",
            "articleUrl3"
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

