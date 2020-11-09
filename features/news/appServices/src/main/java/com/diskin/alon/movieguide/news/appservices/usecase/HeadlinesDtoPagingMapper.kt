package com.diskin.alon.movieguide.news.appservices.usecase

import androidx.paging.PagingData
import androidx.paging.map
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.news.appservices.model.HeadlineDto
import com.diskin.alon.movieguide.news.domain.HeadlineEntity
import javax.inject.Inject

class HeadlinesDtoPagingMapper @Inject constructor() :
    Mapper<PagingData<HeadlineEntity>, PagingData<HeadlineDto>> {
    override fun map(source: PagingData<HeadlineEntity>): PagingData<HeadlineDto> {
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