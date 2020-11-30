package com.diskin.alon.movieguide.news.appservices.usecase

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.appservices.UseCase
import com.diskin.alon.movieguide.news.appservices.data.UnBookmarkArticlesRequest
import com.diskin.alon.movieguide.news.appservices.interfaces.ArticleRepository
import io.reactivex.Single
import javax.inject.Inject

/**
 * Coordinate app operations to un bookmark articles.
 */
class UnBookmarkArticlesUseCase @Inject constructor(
    private val repository: ArticleRepository
    ) : UseCase<UnBookmarkArticlesRequest,Single<Result<Unit>>> {

    override fun execute(param: UnBookmarkArticlesRequest): Single<Result<Unit>> {
        return repository.unBookmark(param.articleIds)
    }
}