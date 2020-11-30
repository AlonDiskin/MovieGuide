package com.diskin.alon.movieguide.news.appservices.usecase

import com.diskin.alon.movieguide.common.appservices.*
import com.diskin.alon.movieguide.news.appservices.data.ArticleDto
import com.diskin.alon.movieguide.news.appservices.data.ArticleRequest
import com.diskin.alon.movieguide.news.appservices.interfaces.ArticleRepository
import com.diskin.alon.movieguide.news.domain.ArticleEntity
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Coordinate app operations to provide an existing article data.
 */
class GetArticleUseCase @Inject constructor(
    private val articleRepository: ArticleRepository
) : UseCase<ArticleRequest, Observable<Result<ArticleDto>>> {

    override fun execute(param: ArticleRequest): Observable<Result<ArticleDto>> =
        Observable.combineLatest(
            articleRepository.get(param.id).toData(),
            articleRepository.isBookmarked(param.id).toData(),
            this::composeArticleDto
        ).toResult()

    private fun composeArticleDto(article: ArticleEntity,isBookmarked: Boolean) =
        ArticleDto(
            article.id,
            article.title,
            article.content,
            article.author,
            article.date,
            article.imageUrl,
            article.articleUrl,
            isBookmarked
        )
}