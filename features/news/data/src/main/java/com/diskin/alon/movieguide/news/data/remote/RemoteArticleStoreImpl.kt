package com.diskin.alon.movieguide.news.data.remote

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.rxjava2.observable
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.appservices.toSingleResult
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.news.data.remote.data.FeedlyArticleId
import com.diskin.alon.movieguide.news.data.remote.data.FeedlyEntryResponse
import com.diskin.alon.movieguide.news.domain.ArticleEntity
import com.diskin.alonmovieguide.common.data.NetworkErrorHandler
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.net.URLEncoder
import javax.inject.Inject

class RemoteArticleStoreImpl @Inject constructor(
    private val api: FeedlyApi,
    private val errorHandler: NetworkErrorHandler,
    private val apiArticleMapper: Mapper<FeedlyEntryResponse,ArticleEntity>
) : RemoteArticleStore {

    override fun getArticle(articleId: String): Observable<Result<ArticleEntity>> {
        return api.getEntry(URLEncoder.encode(articleId, "UTF-8"))
            .subscribeOn(Schedulers.io())
            .map { apiArticleMapper.map(it.first()) }
            .toSingleResult(errorHandler::handle)
            .toObservable()
    }

    override fun getPaging(config: PagingConfig): Observable<PagingData<ArticleEntity>> {
        return Pager(config) { MoviesArticlesPagingSource(api,errorHandler,apiArticleMapper) }
            .observable
    }

    override fun getAll(vararg id: String): Observable<Result<List<ArticleEntity>>> {
        return api.getEntries(id.toList().map { FeedlyArticleId(it) })
            .subscribeOn(Schedulers.io())
            .map { response -> response.map { apiArticleMapper.map(it) } }
            .toSingleResult(errorHandler::handle)
            .toObservable()
    }
}