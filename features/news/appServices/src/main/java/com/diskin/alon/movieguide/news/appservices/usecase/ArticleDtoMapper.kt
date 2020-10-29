package com.diskin.alon.movieguide.news.appservices.usecase

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.common.Mapper
import com.diskin.alon.movieguide.news.appservices.model.ArticleDto
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