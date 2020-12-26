package com.diskin.alon.movieguide.news.appservices.usecase

import com.diskin.alon.movieguide.common.appservices.UseCase
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.news.appservices.data.BookmarkArticleRequest
import com.diskin.alon.movieguide.news.appservices.interfaces.ArticleRepository
import io.reactivex.Single
import javax.inject.Inject

/**
 * Coordinate app operations to bookmark an article.
 */
class BookmarkArticleUseCase @Inject constructor(
    private val repository: ArticleRepository
    ) : UseCase<BookmarkArticleRequest, Single<Result<Unit>>> {

    override fun execute(param: BookmarkArticleRequest): Single<Result<Unit>> {
        return repository.bookmark(param.id)
    }
}