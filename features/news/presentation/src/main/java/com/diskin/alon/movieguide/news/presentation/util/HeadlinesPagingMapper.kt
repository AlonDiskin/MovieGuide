package com.diskin.alon.movieguide.news.presentation.util

import androidx.paging.PagingData
import androidx.paging.map
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.news.appservices.data.HeadlineDto
import com.diskin.alon.movieguide.news.presentation.data.Headline
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Maps app services headlines dto paging, to headlines presentation paging.
 */
class HeadlinesPagingMapper @Inject constructor(
    private val headlineMapper: Mapper<HeadlineDto,Headline>
) : Mapper<Observable<PagingData<HeadlineDto>>, Observable<PagingData<Headline>>> {

    override fun map(source: Observable<PagingData<HeadlineDto>>): Observable<PagingData<Headline>> {
        return source.map { paging -> paging.map { headlineMapper.map(it) } }
    }
}