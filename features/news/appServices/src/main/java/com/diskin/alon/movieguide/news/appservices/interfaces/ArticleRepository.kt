package com.diskin.alon.movieguide.news.appservices.interfaces

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.news.domain.ArticleEntity
import io.reactivex.Observable

interface ArticleRepository {

    fun get(id: String): Observable<Result<ArticleEntity>>
}