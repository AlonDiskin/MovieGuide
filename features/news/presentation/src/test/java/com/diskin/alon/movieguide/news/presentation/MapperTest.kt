package com.diskin.alon.movieguide.news.presentation

import com.diskin.alon.movieguide.news.appservices.model.HeadlineDto
import com.diskin.alon.movieguide.news.presentation.model.NewsHeadline
import com.diskin.alon.movieguide.news.presentation.viewmodel.DATE_FORMAT
import com.diskin.alon.movieguide.news.presentation.viewmodel.mapHeadlineDtoToNewsHeadline
import com.google.common.truth.Truth.assertThat
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.joda.time.LocalDateTime
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Mapper unit tests.
 */
@RunWith(JUnitParamsRunner::class)
class MapperTest {

    @Test
    @Parameters(method = "dtoToHeadlineParams")
    fun testMapHeadlineDtoToNewsHeadline(
        dto: HeadlineDto,
        expectedHeadline: NewsHeadline
    ) {
        // Given

        // When
        val actualMapped = mapHeadlineDtoToNewsHeadline(dto)

        // Then
        assertThat(actualMapped).isEqualTo(expectedHeadline)
    }

    fun dtoToHeadlineParams() =
        arrayOf(
            arrayOf(
                HeadlineDto(
                    "id1",
                    "title1",
                    1600687140000L,
                    "url1",
                    "article1"
                ),
                NewsHeadline(
                    "id1",
                    "title1",
                    LocalDateTime(1600687140000L).toString(DATE_FORMAT),
                    "url1",
                    "article1"
                )
            ),
            arrayOf(
                HeadlineDto(
                    "id2",
                    "title2",
                    1599511800000L,
                    "url2",
                    "article2"
                ),
                NewsHeadline(
                    "id2",
                    "title2",
                    LocalDateTime(1599511800000L).toString(DATE_FORMAT),
                    "url2",
                    "article2"
                )
            )
        )
}