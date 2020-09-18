package com.diskin.alon.movieguide.news.appservices

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.diskin.alon.movieguide.news.domain.HeadlineEntity
import io.reactivex.Observable

/**
 * Repository contract for [HeadlineEntity]
 */
interface HeadlineRepository {

    fun getPaging(config: PagingConfig): Observable<PagingData<HeadlineEntity>>
}