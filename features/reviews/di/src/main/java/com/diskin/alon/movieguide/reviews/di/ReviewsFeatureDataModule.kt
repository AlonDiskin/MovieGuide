package com.diskin.alon.movieguide.reviews.di

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.common.util.Mapper2
import com.diskin.alon.movieguide.reviews.appservices.interfaces.MovieRepository
import com.diskin.alon.movieguide.reviews.appservices.interfaces.MovieReviewRepository
import com.diskin.alon.movieguide.reviews.data.implementations.MovieRepositoryImpl
import com.diskin.alon.movieguide.reviews.data.implementations.MovieReviewRepositoryImpl
import com.diskin.alon.movieguide.reviews.data.remote.MovieMapper
import com.diskin.alon.movieguide.reviews.data.remote.MovieReviewMapper
import com.diskin.alon.movieguide.reviews.data.remote.RemoteMovieReviewSource
import com.diskin.alon.movieguide.reviews.data.remote.RemoteMovieReviewSourceImpl
import com.diskin.alon.movieguide.reviews.data.remote.data.MovieDetailResponse
import com.diskin.alon.movieguide.reviews.data.remote.data.MoviesResponse
import com.diskin.alon.movieguide.reviews.data.remote.data.TrailersResponse
import com.diskin.alon.movieguide.reviews.domain.entities.MovieEntity
import com.diskin.alon.movieguide.reviews.domain.entities.MovieReviewEntity
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class ReviewsFeatureDataModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun provideMovieReviewMapper(): Mapper2<MovieDetailResponse, TrailersResponse, Result<MovieReviewEntity>> {
            return MovieReviewMapper()
        }

        @JvmStatic
        @Provides
        fun provideMovieEntityMapper(): Mapper<List<MoviesResponse.MovieResponse>, List<MovieEntity>> {
            return MovieMapper()
        }
    }

    @Binds
    abstract fun bindMovieRepository(repo: MovieRepositoryImpl): MovieRepository

    @Binds
    abstract fun bindMovieReviewRepository(repo: MovieReviewRepositoryImpl): MovieReviewRepository

    @Binds
    abstract fun bindRemoteMovieReviewSource(source: RemoteMovieReviewSourceImpl): RemoteMovieReviewSource
}