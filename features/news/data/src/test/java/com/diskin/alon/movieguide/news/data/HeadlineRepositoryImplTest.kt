package com.diskin.alon.movieguide.news.data

import androidx.paging.PagingConfig
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

/**
 * [HeadlineRepositoryImpl] unit test class.
 */
class HeadlineRepositoryImplTest {

    // Test subject
    private lateinit var repository: HeadlineRepositoryImpl

    // Collaborators
    private val api: FeedlyApi = mockk()

    @Before
    fun setUp() {
        // Init test subject
        repository = HeadlineRepositoryImpl(api)
    }

    @Test
    fun getHeadlinesPagingWhenQueried() {
        // Given an initialized repository

        // When repository is queried for movie headlines paging
        val testConfig = PagingConfig(pageSize = 10)
        val testObserver = repository.getPaging(testConfig).test()

        // Then repository should return paging that loads headlines from remote
        testObserver.assertValueCount(1)
    }
}