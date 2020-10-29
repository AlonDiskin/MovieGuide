package com.diskin.alon.movieguide.news.presentation.viewmodel

import android.content.res.Resources
import androidx.paging.PagingData
import androidx.paging.map
import com.diskin.alon.movieguide.common.common.Mapper
import com.diskin.alon.movieguide.news.appservices.model.HeadlineDto
import com.diskin.alon.movieguide.news.presentation.R
import com.diskin.alon.movieguide.news.presentation.model.NewsHeadline
import org.joda.time.LocalDateTime
import javax.inject.Inject

/**
 * Maps app services headlines dto paging, to headlines presentation paging.
 */
class HeadlinesPagingMapper @Inject constructor(
    private val resources: Resources
) : Mapper<PagingData<HeadlineDto>, PagingData<NewsHeadline>> {

    override fun map(source: PagingData<HeadlineDto>): PagingData<NewsHeadline> {
        return source.map { dto ->
            NewsHeadline(
                dto.id,
                dto.title,
                LocalDateTime(dto.date).toString(resources.getString(R.string.headline_date_format)),
                dto.imageUrl,
                dto.articleUrl
            )
        }
    }
}