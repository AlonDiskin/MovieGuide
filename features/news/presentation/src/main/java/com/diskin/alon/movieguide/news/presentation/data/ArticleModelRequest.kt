package com.diskin.alon.movieguide.news.presentation.data

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.presentation.ModelRequest
import com.diskin.alon.movieguide.news.appservices.data.ArticleRequest
import io.reactivex.Observable

data class ArticleModelRequest(
    val id: String
) : ModelRequest<ArticleRequest, Observable<Result<Article>>>(
  ArticleRequest(id)
)