package com.diskin.alon.movieguide.news.data.remote

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.news.domain.ArticleEntity
import io.reactivex.Observable

interface RemoteArticleStore {

    fun getArticle(articleId: String): Observable<Result<ArticleEntity>>

    fun getPaging(config: PagingConfig): Observable<PagingData<ArticleEntity>>

    fun getAll(vararg id: String): Observable<Result<List<ArticleEntity>>>
}