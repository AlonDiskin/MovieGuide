package com.diskin.alon.movieguide.news.data

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.news.appservices.data.BookmarkSorting
import com.diskin.alon.movieguide.news.data.local.BookmarkDao
import com.diskin.alon.movieguide.news.data.local.Bookmark
import com.diskin.alon.movieguide.news.data.local.BookmarkedHeadlinesStoreImpl
import com.diskin.alon.movieguide.news.domain.HeadlineEntity
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Observable
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Test
import org.junit.runner.RunWith

/**
 * [BookmarkedHeadlinesStoreImpl] unit test class.
 */
@RunWith(JUnitParamsRunner::class)
class LocalHeadlinesStoreImplTest {

    @Test
    @Parameters(method = "sortParams")
    fun fetchBookmarkedHeadlinesFromDaoWhenQueried(sorting: BookmarkSorting) {
        // Test case fixture
        val dao: BookmarkDao = mockk()
        val mappedHeadlines: Result<List<HeadlineEntity>> = mockk()
        val mapper: Mapper<List<Bookmark>, Result<List<HeadlineEntity>>> = mockk()
        val daoHeadlines: List<Bookmark> = mockk()

        every { dao.getHeadlinesAsc() } returns Observable.just(daoHeadlines)
        every { dao.getHeadlinesDesc() } returns Observable.just(daoHeadlines)
        every { mapper.map(any()) } returns mappedHeadlines

        // Given an initialized store
        val store = BookmarkedHeadlinesStoreImpl(dao,mapper)

        // When store is asked to return all local bookmarked headlines
        val testObserver = store.getBookmarked(sorting).test()

        // Then store should get all headlines from dao
        when(sorting) {
            BookmarkSorting.NEWEST -> {
                verify { dao.getHeadlinesDesc() }
            }

            BookmarkSorting.OLDEST -> {
                verify { dao.getHeadlinesAsc() }
            }
        }

        // And return a mapped result for client
        testObserver.assertValue(mappedHeadlines)
    }

    private fun sortParams() = arrayOf(
        BookmarkSorting.NEWEST,
        BookmarkSorting.OLDEST
    )
}