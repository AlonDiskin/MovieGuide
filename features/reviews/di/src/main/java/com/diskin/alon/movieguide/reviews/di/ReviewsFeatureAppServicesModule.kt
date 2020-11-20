package com.diskin.alon.movieguide.reviews.di

import androidx.paging.PagingData
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.reviews.appservices.data.MovieDto
import com.diskin.alon.movieguide.reviews.appservices.data.MovieReviewDto
import com.diskin.alon.movieguide.reviews.appservices.usecase.MovieDtoPagingMapper
import com.diskin.alon.movieguide.reviews.appservices.usecase.MovieReviewDtoMapper
import com.diskin.alon.movieguide.reviews.domain.entities.MovieEntity
import com.diskin.alon.movieguide.reviews.domain.entities.MovieReviewEntity
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class ReviewsFeatureAppServicesModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun provideMovieReviewDtoMapper(): Mapper<Result<MovieReviewEntity>, Result<MovieReviewDto>> {
            return MovieReviewDtoMapper()
        }
    }

    @Binds
    abstract fun provideMoviePagingDtoMapper(mapper: MovieDtoPagingMapper): Mapper<PagingData<MovieEntity>, PagingData<MovieDto>>
}