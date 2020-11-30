package com.diskin.alon.movieguide.news.presentation.data

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.presentation.ModelRequest
import com.diskin.alon.movieguide.news.appservices.data.UnBookmarkArticlesRequest
import io.reactivex.Single

data class UnBookmarkingModelRequest(
    val ids: List<String>
) : ModelRequest<UnBookmarkArticlesRequest,Single<Result<Unit>>>(
    UnBookmarkArticlesRequest(ids)
)