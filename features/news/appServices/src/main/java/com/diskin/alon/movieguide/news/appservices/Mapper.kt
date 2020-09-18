package com.diskin.alon.movieguide.news.appservices

import androidx.paging.PagingData
import androidx.paging.map
import com.diskin.alon.movieguide.news.domain.HeadlineEntity

object Mapper {

    fun mapPagedHeadlinesToDto(data: PagingData<HeadlineEntity>) = data.map {
        HeadlineDto(
            it.id,
            it.title,
            it.date.timeInMillis,
            it.imageUrl
        )
    }
}