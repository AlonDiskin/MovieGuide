package com.diskin.alon.movieguide.reviews.di.common

import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.common.util.Mapper2
import com.diskin.alon.movieguide.reviews.appservices.interfaces.MovieRepository
import com.diskin.alon.movieguide.reviews.appservices.interfaces.MovieReviewRepository
import com.diskin.alon.movieguide.reviews.data.implementations.MovieRepositoryImpl
import com.diskin.alon.movieguide.reviews.data.implementations.MovieReviewRepositoryImpl
import com.diskin.alon.movieguide.reviews.data.local.*
import com.diskin.alon.movieguide.reviews.data.remote.*
import com.diskin.alon.movieguide.reviews.data.remote.data.MovieDetailResponse
import com.diskin.alon.movieguide.reviews.data.remote.data.MoviesResponse
import com.diskin.alon.movieguide.reviews.data.remote.data.TrailersResponse
import com.diskin.alon.movieguide.reviews.domain.entities.MovieEntity
import com.diskin.alon.movieguide.reviews.domain.entities.MovieReviewEntity
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class ReviewsDataModule {

    @Module
    companion object {

        @JvmStatic
        @Singleton
        @Provides
        fun provideMovieResponseMapper(): Mapper<MoviesResponse.MovieResponse,MovieEntity> {
            return MovieMapper()
        }
    }

    @Binds
    abstract fun bindMovieRepository(repo: MovieRepositoryImpl): MovieRepository

    @Binds
    abstract fun bindMovieStore(store: MovieStoreImpl): MovieStore

    @Binds
    abstract fun provideFavoriteMoviesStore(store: FavoriteMoviesStoreImpl): FavoriteMoviesStore

    @Binds
    abstract fun bindsStorageErrorHandler(handler: StorageErrorHandlerImpl): StorageErrorHandler

    @Binds
    abstract fun provideFavoriteMovieMapper(mapper: FavoriteMovieMapper): Mapper<MovieEntity, FavoriteMovie>

    @Binds
    abstract fun bindMovieReviewRepository(repo: MovieReviewRepositoryImpl): MovieReviewRepository

    @Binds
    abstract fun bindRemoteMovieReviewSource(source: MovieReviewStoreImpl): MovieReviewStore

    @Binds
    abstract fun bindMovieReviewMapper(mapper: MovieReviewMapper): Mapper2<MovieDetailResponse, TrailersResponse, MovieReviewEntity>
}