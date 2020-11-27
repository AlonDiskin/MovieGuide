package com.diskin.alon.movieguide.news.data

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.news.data.remote.util.ApiArticleMapper
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

/**
 * [ApiArticleMapper] unit test class.
 */
class ApiArticleMapperTest {

    // Test subject
    private lateinit var mapper: ApiArticleMapper

    @Before
    fun setUp() {
        mapper = ApiArticleMapper()
    }

    @Test
    fun mapToArticleResult() {
        // Given an initialized mapper

        // When mapper is asked to map api entry response
        val entry = createApiEntryResponse()
        val actual = mapper.map(entry)

        // Then mapper should map response to entity result
        val expected = Result.Success(getArticleEntity(entry))
        assertThat(actual).isEqualTo(expected)
    }
}