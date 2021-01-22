package com.diskin.alon.movieguide.news.presentation.util

import android.content.Context
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.appservices.mapResult
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.news.appservices.data.ArticleDto
import com.diskin.alon.movieguide.news.presentation.R
import com.diskin.alon.movieguide.news.presentation.data.Article
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.Observable
import org.joda.time.LocalDateTime
import javax.inject.Inject

/**
 * Maps app services article dto, to article presentation model.
 */
class ArticleMapper @Inject constructor(
    @ApplicationContext val app: Context
) : Mapper<Observable<Result<ArticleDto>>,Observable<Result<Article>>> {

    override fun map(source: Observable<Result<ArticleDto>>): Observable<Result<Article>> {
        return source.mapResult{
            Article(
                it.title,
                it.author,
                it.content,
                LocalDateTime(it.date).toString(app.resources.getString(R.string.article_date_format)),
                it.imageUrl,
                it.articleUrl,
                it.bookmarked
            )
        }
    }
}