package com.diskin.alon.movieguide.news.appservices

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.news.appservices.model.ArticleDto
import com.diskin.alon.movieguide.news.appservices.usecase.ArticleDtoMapper
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

/**
 * [ArticleDtoMapper] unit test class.
 */
class ArticleDtoMapperTest {

    // Test subject
    private lateinit var mapper: ArticleDtoMapper

    @Before
    fun setUp() {
        mapper = ArticleDtoMapper()
    }

    @Test
    fun mapArticleWhenResultSuccessful() {
        // Given an initialized mapper

        // When mapper is asked to map a successful result of article entity
        val entity = createArticleEntity()
        val actual = mapper.map(Result.Success(entity))

        // Then mapper should map entity result to dto result
        val expected = mapArticleDtoSuccessResult(entity)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun mapArticleWhenResultError() {
        // Given an initialized mapper

        // When mapper is asked to map an error result of article entity
        val error = createAppError()
        val actual = mapper.map(Result.Error(error))

        // Then mapper should map entity result to dto error
        val expected = Result.Error<ArticleDto>(error)
        assertThat(actual).isEqualTo(expected)
    }
}