package com.diskin.alon.movieguide.news.data.local

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.news.appservices.data.BookmarkSorting
import com.diskin.alon.movieguide.news.domain.HeadlineEntity
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Handle data operations to provide movie news headlines from local on device sources.
 */
class BookmarkedHeadlinesStoreImpl @Inject constructor(
    private val dao: BookmarkDao,
    private val mapper: Mapper<List<Bookmark>,Result<List<HeadlineEntity>>>
) : BookmarkedHeadlinesStore {

    override fun getBookmarked(sorting: BookmarkSorting): Observable<Result<List<HeadlineEntity>>> {
        return when(sorting) {
            BookmarkSorting.NEWEST -> {
                dao.getHeadlinesDesc()
                    .map { mapper.map(it) }
            }

            BookmarkSorting.OLDEST -> {
                dao.getHeadlinesAsc()
                    .map { mapper.map(it) }
            }
        }
    }
}