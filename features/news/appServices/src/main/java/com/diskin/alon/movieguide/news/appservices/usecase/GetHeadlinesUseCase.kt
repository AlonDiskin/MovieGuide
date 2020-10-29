package com.diskin.alon.movieguide.news.appservices.usecase

import androidx.paging.PagingData
import com.diskin.alon.movieguide.common.appservices.UseCase
import com.diskin.alon.movieguide.common.common.Mapper
import com.diskin.alon.movieguide.news.appservices.interfaces.HeadlineRepository
import com.diskin.alon.movieguide.news.appservices.model.HeadlineDto
import com.diskin.alon.movieguide.news.appservices.model.HeadlinesRequest
import com.diskin.alon.movieguide.news.domain.HeadlineEntity
import io.reactivex.Observable
import javax.inject.Inject

class GetHeadlinesUseCase @Inject constructor(
    private val repository: HeadlineRepository,
    private val headlineMapper: Mapper<PagingData<HeadlineEntity>, PagingData<HeadlineDto>>
) : UseCase<HeadlinesRequest,Observable<PagingData<HeadlineDto>>> {

    override fun execute(param: HeadlinesRequest): Observable<PagingData<HeadlineDto>> {
        return repository.getPaging(param.pagingConfig)
            .map(headlineMapper::map)
    }
}
