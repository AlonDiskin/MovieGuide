package com.diskin.alon.movieguide.news.presentation

import com.diskin.alon.movieguide.news.appservices.HeadlineDto
import com.google.common.truth.Truth.assertThat
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Test
import org.junit.runner.RunWith

/**
 * [mapDtoPagingToNewsHeadline] unit tests.
 */
@RunWith(JUnitParamsRunner::class)
class MapperTest {

    @Test
    @Parameters(method = "dtoToHeadlineParams")
    fun testMapHeadlineDtoToNewsHeadline(
        dto: HeadlineDto,
        expectedHeadline: NewsHeadline) {
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
                    "url1"
                ),
                NewsHeadline(
                    "id1",
                    "title1",
                    "21 Sep 14:19",
                    "url1"
                )
            ),
            arrayOf(
                HeadlineDto(
                    "id2",
                    "title2",
                    1599511800000L,
                    "url2"
                ),
                NewsHeadline(
                    "id2",
                    "title2",
                    "07 Sep 23:50",
                    "url2"
                )
            )
        )
}