package com.diskin.alon.movieguide.news.appservices.util

import androidx.paging.PagingData
import androidx.paging.map
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.news.appservices.data.HeadlineDto
import com.diskin.alon.movieguide.news.domain.ArticleEntity
import javax.inject.Inject

class HeadlinesDtoPagingMapper @Inject constructor() : Mapper<PagingData<ArticleEntity>, PagingData<HeadlineDto>> {
    override fun map(source: PagingData<ArticleEntity>): PagingData<HeadlineDto> {
        return source.map {
            HeadlineDto(
                it.id,
                it.title,
                it.date,
                it.imageUrl,
                it.articleUrl
            )
        }
    }
}