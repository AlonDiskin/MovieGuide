package com.diskin.alon.movieguide.news.appservices.usecase

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.appservices.UseCase
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.news.appservices.data.BookmarksRequest
import com.diskin.alon.movieguide.news.appservices.data.HeadlineDto
import com.diskin.alon.movieguide.news.appservices.interfaces.ArticleRepository
import com.diskin.alon.movieguide.news.domain.ArticleEntity
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Coordinate app operations to provide the headlines of all bookmarked articles.
 */
class GetBookmarkedHeadlinesUseCase @Inject constructor(
    private val repository: ArticleRepository,
    private val mapper: Mapper<Result<List<ArticleEntity>>,Result<List<HeadlineDto>>>
) : UseCase<BookmarksRequest,Observable<Result<List<HeadlineDto>>>> {

    override fun execute(param: BookmarksRequest): Observable<Result<List<HeadlineDto>>> {
        return repository.getBookmarked(param.sorting)
            .map { mapper.map(it) }
    }
}