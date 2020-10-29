package com.diskin.alon.movieguide.news.data.remote

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.news.domain.ArticleEntity
import io.reactivex.Observable

interface RemoteArticleStore {

    fun getArticle(articleId: String): Observable<Result<ArticleEntity>>
}