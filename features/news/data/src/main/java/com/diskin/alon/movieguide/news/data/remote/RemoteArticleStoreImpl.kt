package com.diskin.alon.movieguide.news.data.remote

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.rxjava2.observable
import com.diskin.alon.movieguide.common.appservices.Result
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
            .map(this::mapApiResponseToArticle)
            .onErrorReturn { Result.Error(errorHandler.handle(it)) }
            .toObservable()
    }

    override fun getPaging(config: PagingConfig): Observable<PagingData<ArticleEntity>> {
        return Pager(config)
        { MoviesHeadlinesPagingSource(api,errorHandler) }
            .observable
    }

    override fun getAll(vararg id: String): Observable<Result<List<ArticleEntity>>> {
        val articleIds = id.toList().map { FeedlyArticleId(it) }

        return api.getEntries(articleIds)
            .subscribeOn(Schedulers.io())
            .map(this::mapApiResponseToArticles)
            .onErrorReturn { Result.Error(errorHandler.handle(it)) }
            .toObservable()
    }

    private fun mapApiResponseToArticle(apiResponse: List<FeedlyEntryResponse>): Result<ArticleEntity> {
        val apiArticle = apiResponse.first()

        return Result.Success(apiArticleMapper.map(apiArticle))
    }

    private fun mapApiResponseToArticles(apiResponse: List<FeedlyEntryResponse>): Result<List<ArticleEntity>> {
        return Result.Success(apiResponse.map { apiArticleMapper.map(it) })
    }
}