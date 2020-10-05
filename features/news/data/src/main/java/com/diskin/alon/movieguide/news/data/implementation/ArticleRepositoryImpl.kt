package com.diskin.alon.movieguide.news.data.implementation

import com.diskin.alon.movieguide.news.appservices.interfaces.ArticleRepository
import com.diskin.alon.movieguide.news.data.remote.FeedlyApi
import com.diskin.alon.movieguide.news.domain.ArticleEntity
import io.reactivex.Observable
import javax.inject.Inject

class ArticleRepositoryImpl @Inject constructor(
    private val api: FeedlyApi
) : ArticleRepository {

    override fun get(id: String): Observable<ArticleEntity> {
        return api.getEntry(cleanId(id))
            .map { mapApiEntryResponseToArticleEntity(it.first()) }
            .toObservable()
    }
}