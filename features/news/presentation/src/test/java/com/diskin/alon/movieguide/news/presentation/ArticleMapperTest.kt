package com.diskin.alon.movieguide.news.presentation

import android.content.res.Resources
import com.diskin.alon.movieguide.news.appservices.model.ArticleDto
import com.diskin.alon.movieguide.news.presentation.model.Article
import com.diskin.alon.movieguide.news.presentation.viewmodel.ArticleMapper
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * [ArticleMapper] unit test class.
 */
@RunWith(JUnitParamsRunner::class)
class ArticleMapperTest {

    // Test subject
    private lateinit var mapper: ArticleMapper

    // Collaborators
    private val resources: Resources = mockk()

    @Before
    fun setUp() {
        // Init subject
        mapper = ArticleMapper(resources)
    }

    @Test
    @Parameters(method = "mapParams")
    fun mapAppDtoToPresentationModel(
        dto: ArticleDto,
        dateFormat: String,
        article: Article) {

        // Test case fixture
        every { resources.getString(R.string.article_date_format) } returns dateFormat

        // Given an initialized mapper

        // When mapper is asked to  map dto
        val actual = mapper.map(dto)

        // Then mapper should map dto to presentation article
        assertThat(actual).isEqualTo(article)
    }

    private fun mapParams() = arrayOf(
        arrayOf(ArticleDto("title1",
            "content1",
            "author1",
            LocalDate(2020,10,12).toDate().time,
            "image_url1",
            "article_url1"),
            "MM dd yyyy",
            Article(
                "title1",
                "author1",
                "content1",
                "10 12 2020",
                "image_url1",
                "article_url1"
            )
        ),
        arrayOf(ArticleDto("title_2",
            "content_2",
            "author_2",
            LocalDateTime(2020,3,2,14,50).toDate().time,
            "image_url2",
            "article_url2"),
            "dd MMM HH:mm",
            Article(
                "title_2",
                "author_2",
                "content_2",
                "02 Mar 14:50",
                "image_url2",
                "article_url2"
            )
        )
    )
}