package com.diskin.alon.movieguide.news.presentation.util

import android.content.res.Resources
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.news.appservices.data.ArticleDto
import com.diskin.alon.movieguide.news.presentation.R
import com.diskin.alon.movieguide.news.presentation.data.Article
import io.reactivex.Observable
import org.joda.time.LocalDateTime
import javax.inject.Inject

/**
 * Maps app services article dto, to article presentation model.
 */
class ArticleMapper @Inject constructor(
    private val resources: Resources
) : Mapper<Observable<Result<ArticleDto>>,Observable<Result<Article>>> {

    override fun map(source: Observable<Result<ArticleDto>>): Observable<Result<Article>> {
        return source.map {
            when(it) {
                is Result.Success -> Result.Success(
                    Article(
                        it.data.title,
                        it.data.author,
                        it.data.content,
                        LocalDateTime(it.data.date).toString(resources.getString(R.string.article_date_format)),
                        it.data.imageUrl,
                        it.data.articleUrl
                    )
                )

                is Result.Error -> Result.Error(it.error)
            }
        }
    }
}