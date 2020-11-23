package com.diskin.alon.movieguide.news.presentation.util

import android.content.res.Resources
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.news.appservices.data.ArticleDto
import com.diskin.alon.movieguide.news.presentation.R
import com.diskin.alon.movieguide.news.presentation.data.Article
import org.joda.time.LocalDateTime
import javax.inject.Inject

/**
 * Maps app services article dto, to article presentation model.
 */
class ArticleMapper @Inject constructor(
    private val resources: Resources
) : Mapper<ArticleDto, Article> {
    override fun map(source: ArticleDto): Article {
        return Article(
            source.title,
            source.author,
            source.content,
            LocalDateTime(source.date).toString(resources.getString(R.string.article_date_format)),
            source.imageUrl,
            source.articleUrl
        )
    }
}