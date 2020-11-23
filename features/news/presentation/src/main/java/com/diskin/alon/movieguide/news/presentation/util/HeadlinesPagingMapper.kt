package com.diskin.alon.movieguide.news.presentation.util

import android.content.res.Resources
import androidx.paging.PagingData
import androidx.paging.map
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.news.appservices.data.HeadlineDto
import com.diskin.alon.movieguide.news.presentation.R
import com.diskin.alon.movieguide.news.presentation.data.Headline
import org.joda.time.LocalDateTime
import javax.inject.Inject

/**
 * Maps app services headlines dto paging, to headlines presentation paging.
 */
class HeadlinesPagingMapper @Inject constructor(
    private val resources: Resources
) : Mapper<PagingData<HeadlineDto>, PagingData<Headline>> {

    override fun map(source: PagingData<HeadlineDto>): PagingData<Headline> {
        return source.map { dto ->
            Headline(
                dto.id,
                dto.title,
                LocalDateTime(dto.date).toString(resources.getString(R.string.headline_date_format)),
                dto.imageUrl,
                dto.articleUrl
            )
        }
    }
}