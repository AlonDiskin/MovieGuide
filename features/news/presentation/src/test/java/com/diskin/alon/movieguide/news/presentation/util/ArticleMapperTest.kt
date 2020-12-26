package com.diskin.alon.movieguide.news.presentation.util

import android.content.res.Resources
import com.diskin.alon.movieguide.common.appservices.AppError
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.news.appservices.data.ArticleDto
import com.diskin.alon.movieguide.news.presentation.R
import com.diskin.alon.movieguide.news.presentation.data.Article
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Observable
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
        dtoRes: Result<ArticleDto>,
        dateFormat: String,
        articleRes: Result<Article>) {

        // Test case fixture
        every { resources.getString(R.string.article_date_format) } returns dateFormat

        // Given an initialized mapper

        // When mapper is asked to  map dto
        val testObserver = mapper.map(Observable.just(dtoRes)).test()

        // Then mapper should map dto to presentation article
        testObserver.assertValue(articleRes)
    }

    private fun mapParams() = arrayOf(
        arrayOf(Result.Success(
            ArticleDto("id1",
                "title1",
                "content1",
                "author1",
                LocalDate(2020,10,12).toDate().time,
                "image_url1",
                "article_url1",
                true
            )
        ) as Result<ArticleDto>,
            "MM dd yyyy",
            Result.Success(
                Article(
                    "title1",
                    "author1",
                    "content1",
                    "10 12 2020",
                    "image_url1",
                    "article_url1",
                    true
                )
            ) as Result<Article>
        ),
        arrayOf(
            Result.Success(
                ArticleDto("id2",
                    "title_2",
                    "content_2",
                    "author_2",
                    LocalDateTime(2020,3,2,14,50).toDate().time,
                    "image_url2",
                    "article_url2",
                    false
                )
            ) as Result<ArticleDto>,
            "dd MMM HH:mm",
            Result.Success(
                Article(
                    "title_2",
                    "author_2",
                    "content_2",
                    "02 Mar 14:50",
                    "image_url2",
                    "article_url2",
                    false
                )
            ) as Result<Article>
        ),
        arrayOf(
            Result.Error<ArticleDto>(AppError("message",true)) as Result<ArticleDto>,
            "dd MMM HH:mm",
            Result.Error<Article>(AppError("message",true)) as Result<Article>
        )
    )
}