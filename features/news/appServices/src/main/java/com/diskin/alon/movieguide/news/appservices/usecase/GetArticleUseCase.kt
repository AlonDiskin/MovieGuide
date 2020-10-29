package com.diskin.alon.movieguide.news.appservices.usecase

import com.diskin.alon.movieguide.common.appservices.UseCase
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.common.Mapper
import com.diskin.alon.movieguide.news.appservices.interfaces.ArticleRepository
import com.diskin.alon.movieguide.news.appservices.model.ArticleDto
import com.diskin.alon.movieguide.news.appservices.model.ArticleRequest
import com.diskin.alon.movieguide.news.domain.ArticleEntity
import io.reactivex.Observable
import javax.inject.Inject

class GetArticleUseCase @Inject constructor(
    private val articleRepository: ArticleRepository,
    private val articleMapper: Mapper<Result<ArticleEntity>, Result<ArticleDto>>
) : UseCase<ArticleRequest, Observable<Result<ArticleDto>>> {

    override fun execute(param: ArticleRequest): Observable<Result<ArticleDto>> =
        articleRepository.get(param.id)
            .map(articleMapper::map)
}