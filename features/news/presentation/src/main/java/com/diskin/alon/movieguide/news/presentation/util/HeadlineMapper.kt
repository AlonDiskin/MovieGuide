package com.diskin.alon.movieguide.news.presentation.util

import android.content.res.Resources
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.news.appservices.data.HeadlineDto
import com.diskin.alon.movieguide.news.presentation.R
import com.diskin.alon.movieguide.news.presentation.data.Headline
import org.joda.time.LocalDateTime
import javax.inject.Inject

class HeadlineMapper @Inject constructor(
    private val resources: Resources
) : Mapper<HeadlineDto,Headline> {
    override fun map(source: HeadlineDto): Headline {
        return Headline(
            source.id,
            source.title,
            LocalDateTime(source.date).toString(resources.getString(R.string.headline_date_format)),
            source.imageUrl,
            source.articleUrl
        )
    }
}