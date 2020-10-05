package com.diskin.alon.movieguide.news.appservices.usecase

import com.diskin.alon.movieguide.common.appservices.UseCase
import com.diskin.alon.movieguide.news.appservices.usecase.Mapper.mapArticleEntity
import com.diskin.alon.movieguide.news.appservices.interfaces.ArticleRepository
import com.diskin.alon.movieguide.news.appservices.model.ArticleDto
import com.diskin.alon.movieguide.news.appservices.model.ArticleRequest
import io.reactivex.Observable
import javax.inject.Inject

class GetArticleUseCase @Inject constructor(
    private val articleRepository: ArticleRepository
) : UseCase<ArticleRequest, Observable<ArticleDto>> {
    override fun execute(param: ArticleRequest): Observable<ArticleDto> {
        return articleRepository.get(param.id)
            .map(::mapArticleEntity)
    }
}