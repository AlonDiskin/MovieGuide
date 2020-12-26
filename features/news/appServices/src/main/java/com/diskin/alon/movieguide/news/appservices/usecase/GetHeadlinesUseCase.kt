package com.diskin.alon.movieguide.news.appservices.usecase

import androidx.paging.PagingData
import com.diskin.alon.movieguide.common.appservices.UseCase
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.news.appservices.data.HeadlineDto
import com.diskin.alon.movieguide.news.appservices.data.HeadlinesRequest
import com.diskin.alon.movieguide.news.appservices.interfaces.ArticleRepository
import com.diskin.alon.movieguide.news.domain.ArticleEntity
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Coordinate app operations to provide a paging of articles headlines.
 */
class GetHeadlinesUseCase @Inject constructor(
    private val repository: ArticleRepository,
    private val mapper: Mapper<PagingData<ArticleEntity>, PagingData<HeadlineDto>>
) : UseCase<HeadlinesRequest,Observable<PagingData<HeadlineDto>>> {

    override fun execute(param: HeadlinesRequest): Observable<PagingData<HeadlineDto>> {
        return repository.getPaging(param.pagingConfig)
            .map(mapper::map)
    }
}
