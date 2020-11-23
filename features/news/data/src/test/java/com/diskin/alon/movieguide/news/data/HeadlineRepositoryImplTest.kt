package com.diskin.alon.movieguide.news.data

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.news.appservices.data.BookmarkSorting
import com.diskin.alon.movieguide.news.data.implementation.HeadlineRepositoryImpl
import com.diskin.alon.movieguide.news.data.local.BookmarkedHeadlinesStore
import com.diskin.alon.movieguide.news.data.remote.RemoteHeadlinesStore
import com.diskin.alon.movieguide.news.domain.HeadlineEntity
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test

/**
 * [HeadlineRepositoryImpl] unit test class.
 */
class HeadlineRepositoryImplTest {

    // Test subject
    private lateinit var repository: HeadlineRepositoryImpl

    // Collaborators
    private val remoteStore: RemoteHeadlinesStore = mockk()
    private val localStore: BookmarkedHeadlinesStore = mockk()

    @Before
    fun setUp() {
        // Init test subject
        repository = HeadlineRepositoryImpl(remoteStore, localStore)
    }

    @Test
    fun getHeadlinesPagingWhenQueried() {
        // Test case fixture
        val remotePaging: Observable<PagingData<HeadlineEntity>> = mockk()

        every { remoteStore.getAll(any()) } returns remotePaging

        // Given an initialized repository

        // When repository is queried for movie headlines paging
        val config: PagingConfig = mockk()
        val actualPaging = repository.getPaging(config)

        // Then repository should return paging from remote headlines store
        verify { remoteStore.getAll(config) }
        assertThat(actualPaging).isEqualTo(remotePaging)
    }

    @Test
    fun fetchBookmarkedFromLocalStoreWhenQueried() {
        // Test case fixture
        val localHeadlines: Observable<Result<List<HeadlineEntity>>> = mockk()

        every { localStore.getBookmarked(any()) } returns localHeadlines

        // Given an initialized repository

        // When repository is queried for bookmarked headlines
        val sorting: BookmarkSorting = mockk()
        val actualHeadlines = repository.getBookmarked(sorting)

        // Then repository should return headlines from local store
        verify { localStore.getBookmarked(sorting) }
        assertThat(actualHeadlines).isEqualTo(localHeadlines)
    }
}