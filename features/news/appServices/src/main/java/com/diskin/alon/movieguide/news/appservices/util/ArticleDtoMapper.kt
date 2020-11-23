package com.diskin.alon.movieguide.news.appservices.util

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.news.appservices.data.ArticleDto
import com.diskin.alon.movieguide.news.domain.ArticleEntity
import javax.inject.Inject

class ArticleDtoMapper @Inject constructor() : Mapper<Result<ArticleEntity>, Result<ArticleDto>> {

    override fun map(source: Result<ArticleEntity>): Result<ArticleDto> {
        return when (source) {
            is Result.Success -> Result.Success(
                ArticleDto(
                    source.data.title,
                    source.data.content,
                    source.data.author,
                    source.data.date,
                    source.data.imageUrl,
                    source.data.articleUrl
                )
            )

            is Result.Error -> Result.Error(source.error)
        }
    }
}