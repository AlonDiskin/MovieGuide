package com.diskin.alon.movieguide.news.data.remote

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.news.domain.ArticleEntity
import com.diskin.alonmovieguide.common.data.NetworkErrorHandler
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.net.URLEncoder
import javax.inject.Inject

class RemoteNewsStore @Inject constructor(
    private val api: FeedlyApi,
    private val apiResultMapper: Mapper<List<FeedlyEntryResponse>, Result<ArticleEntity>>,
    private val errorHandler: NetworkErrorHandler
) : RemoteArticleStore {

    override fun getArticle(articleId: String): Observable<Result<ArticleEntity>> {
        return api.getEntry(URLEncoder.encode(articleId, "UTF-8"))
            .subscribeOn(Schedulers.io())
            .map(apiResultMapper::map)
            .onErrorReturn { Result.Error(errorHandler.handle(it)) }
            .toObservable()
    }
}