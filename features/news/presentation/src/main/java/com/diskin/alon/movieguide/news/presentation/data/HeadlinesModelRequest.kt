package com.diskin.alon.movieguide.news.presentation.data

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.diskin.alon.movieguide.common.presentation.ModelRequest
import com.diskin.alon.movieguide.news.appservices.data.HeadlinesRequest
import io.reactivex.Observable

data class HeadlinesModelRequest(
    val pagingConfig: PagingConfig
) : ModelRequest<HeadlinesRequest, Observable<PagingData<Headline>>>(
    HeadlinesRequest(pagingConfig)
)