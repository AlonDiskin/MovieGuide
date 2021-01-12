package com.diskin.alon.movieguide.reviews.presentation.util

import android.content.res.Resources
import com.diskin.alon.movieguide.common.appservices.AppError
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.reviews.appservices.data.MovieReviewDto
import com.diskin.alon.movieguide.reviews.appservices.data.TrailerDto
import com.diskin.alon.movieguide.reviews.presentation.R
import com.diskin.alon.movieguide.reviews.presentation.data.MovieReview
import com.diskin.alon.movieguide.reviews.presentation.data.Trailer
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Observable
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.joda.time.LocalDate
import org.junit.Test
import org.junit.runner.RunWith

/**
 * [MovieReviewMapper] unit test class.
 */
@RunWith(JUnitParamsRunner::class)
class MovieReviewMapperTest {

    @Test
    @Parameters(method = "mapParams")
    fun mapAppServicesReviewDataModelToPresentationReviewDataModel(
        appServiceData: Observable<Result<MovieReviewDto>>,
        dateFormat: String,
        presentationData: Result<MovieReview>
    ) {
        // Test case fixture
        val resources = mockk<Resources>()

        every { resources.getString(R.string.movie_release_date_format) } returns dateFormat

        // Given an initialized mapper
        val mapper = MovieReviewMapper(resources)

        // When mapper is asked to map app services review data model
        val testObserver = mapper.map(appServiceData).test()

        // Then mapper should map dto to review presentation data model
        testObserver.assertValue(presentationData)
    }

    private fun mapParams() = arrayOf(
        arrayOf(
            Observable.just(
                Result.Success(
                    MovieReviewDto(
                        "id",
                        "title",
                        8.9,
                        LocalDate(2020,3,2).toDate().time,
                        "backdrop_url",
                        listOf("horror","comedy"),
                        "summary",
                        "review",
                        "webUrl",
                        listOf(TrailerDto("url1","url2")),
                        true
                    )
                )
            ),
            "dd MMM yyyy",
            Result.Success(
                MovieReview(
                    "id",
                    "title",
                    "8.9",
                    "horror,comedy",
                    "02 Mar 2020",
                    "summary",
                    "review",
                    "backdrop_url",
                    "webUrl",
                    listOf(Trailer("url1","url2")),
                    true
                )
            )
        ),
        arrayOf(
            Observable.just(Result.Error<MovieReviewDto>(AppError("message",false))),
            "dd MMM yyyy",
            Result.Error<MovieReview>(AppError("message",false))
        ),
        arrayOf(
            Observable.just(Result.Error<MovieReviewDto>(AppError("message",true))),
            "dd MMM yyyy",
            Result.Error<MovieReview>(AppError("message",true))
        )
    )
}