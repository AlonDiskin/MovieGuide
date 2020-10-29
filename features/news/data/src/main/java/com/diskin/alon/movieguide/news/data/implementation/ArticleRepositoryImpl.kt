package com.diskin.alon.movieguide.news.data.implementation

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.news.appservices.interfaces.ArticleRepository
import com.diskin.alon.movieguide.news.data.remote.RemoteArticleStore
import com.diskin.alon.movieguide.news.domain.ArticleEntity
import io.reactivex.Observable
import javax.inject.Inject

class ArticleRepositoryImpl @Inject constructor(
    private val articleStore: RemoteArticleStore
) : ArticleRepository {

    override fun get(id: String): Observable<Result<ArticleEntity>> {
        return articleStore.getArticle(id)
    }
}