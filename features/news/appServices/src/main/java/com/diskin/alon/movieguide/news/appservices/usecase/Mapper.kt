package com.diskin.alon.movieguide.news.appservices.usecase

import androidx.paging.PagingData
import androidx.paging.map
import com.diskin.alon.movieguide.news.appservices.model.ArticleDto
import com.diskin.alon.movieguide.news.appservices.model.HeadlineDto
import com.diskin.alon.movieguide.news.domain.ArticleEntity
import com.diskin.alon.movieguide.news.domain.HeadlineEntity

object Mapper {

    fun mapPagedHeadlinesToDto(data: PagingData<HeadlineEntity>) = data.map {
        HeadlineDto(
            it.id,
            it.title,
            it.date.timeInMillis,
            it.imageUrl,
            it.articleUrl
        )
    }

    fun mapArticleEntity(entity: ArticleEntity) =
        ArticleDto(
            entity.id,
            entity.title,
            entity.content,
            entity.author,
            entity.date.timeInMillis,
            entity.imageUrl,
            entity.articleUrl
        )
}