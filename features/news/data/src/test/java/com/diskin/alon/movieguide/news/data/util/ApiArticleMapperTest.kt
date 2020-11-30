package com.diskin.alon.movieguide.news.data.util

import com.diskin.alon.movieguide.news.data.remote.data.FeedlyEntryResponse
import com.diskin.alon.movieguide.news.data.remote.util.ApiArticleMapper
import com.diskin.alon.movieguide.news.domain.ArticleEntity
import com.google.common.truth.Truth.assertThat
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * [ApiArticleMapper] unit test class.
 */
@RunWith(JUnitParamsRunner::class)
class ApiArticleMapperTest {

    // Test subject
    private lateinit var mapper: ApiArticleMapper

    @Before
    fun setUp() {
        mapper = ApiArticleMapper()
    }

    @Test
    @Parameters(method = "mapParams")
    fun mapToArticleResult(
        apiEntry: FeedlyEntryResponse,
        articleEntity: ArticleEntity
    ) {
        // Given an initialized mapper

        // When mapper is asked to map api entry response
        val actual = mapper.map(apiEntry)

        // Then mapper should map response to entity result
        assertThat(actual).isEqualTo(articleEntity)
    }

    private fun mapParams() = arrayOf(
        arrayOf(
            FeedlyEntryResponse(
                "id",
                "title",
                "author",
                100L,
                FeedlyEntryResponse.Visual("url"),
                "originId",
                FeedlyEntryResponse.Summary("content")
            ),
            ArticleEntity(
                "id",
                "title",
                "content" ,
                "author",
                100L,
                "url",
                "originId"
            )
        ),
        arrayOf(
            FeedlyEntryResponse(
                "id",
                null,
                null,
                100L,
                null,
                "originId",
                null
            ),
            ArticleEntity(
                "id",
                "",
                "" ,
                "",
                100L,
                "",
                "originId"
            )
        )
    )
}