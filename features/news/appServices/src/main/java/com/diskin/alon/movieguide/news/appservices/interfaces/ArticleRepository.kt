package com.diskin.alon.movieguide.news.appservices.interfaces

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.news.appservices.data.BookmarkSorting
import com.diskin.alon.movieguide.news.domain.ArticleEntity
import io.reactivex.Observable
import io.reactivex.Single

interface ArticleRepository {

    fun get(id: String): Observable<Result<ArticleEntity>>

    fun getPaging(config: PagingConfig): Observable<PagingData<ArticleEntity>>

    fun getBookmarked(sorting: BookmarkSorting): Observable<Result<List<ArticleEntity>>>

    fun bookmark(id: String): Single<Result<Unit>>

    fun unBookmark(ids: List<String>): Single<Result<Unit>>

    fun isBookmarked(id: String): Observable<Result<Boolean>>
}
