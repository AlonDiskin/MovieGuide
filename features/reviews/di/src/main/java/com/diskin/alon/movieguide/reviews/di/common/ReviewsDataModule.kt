package com.diskin.alon.movieguide.reviews.di.common

import androidx.paging.PagingData
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.common.util.Mapper2
import com.diskin.alon.movieguide.reviews.appservices.data.MovieDto
import com.diskin.alon.movieguide.reviews.appservices.interfaces.MovieRepository
import com.diskin.alon.movieguide.reviews.appservices.interfaces.MovieReviewRepository
import com.diskin.alon.movieguide.reviews.appservices.usecase.MovieDtoPagingMapper
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
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ReviewsDataModule {

    companion object {

        @Singleton
        @Provides
        fun provideMovieResponseMapper(): Mapper<MoviesResponse.MovieResponse,MovieEntity> {
            return MovieMapper()
        }
    }

    @Singleton
    @Binds
    abstract fun bindMovieRepository(repo: MovieRepositoryImpl): MovieRepository

    @Singleton
    @Binds
    abstract fun bindMovieStore(store: MovieStoreImpl): MovieStore

    @Singleton
    @Binds
    abstract fun provideFavoriteMoviesStore(store: FavoriteMoviesStoreImpl): FavoriteMoviesStore

    @Singleton
    @Binds
    abstract fun bindsStorageErrorHandler(handler: StorageErrorHandlerImpl): StorageErrorHandler

    @Singleton
    @Binds
    abstract fun provideFavoriteMovieMapper(mapper: FavoriteMovieMapper): Mapper<MovieEntity, FavoriteMovie>

    @Singleton
    @Binds
    abstract fun bindMovieReviewRepository(repo: MovieReviewRepositoryImpl): MovieReviewRepository

    @Binds
    abstract fun bindRemoteMovieReviewSource(source: MovieReviewStoreImpl): MovieReviewStore

    @Singleton
    @Binds
    abstract fun bindMovieReviewMapper(mapper: MovieReviewMapper): Mapper2<MovieDetailResponse, TrailersResponse, MovieReviewEntity>

    @Singleton
    @Binds
    abstract fun bindEntityMovieMapper(mapper: MovieEntityMapper): Mapper<FavoriteMovie,MovieEntity>

    @Singleton
    @Binds
    abstract fun provideMoviePagingDtoMapper(mapper: MovieDtoPagingMapper): Mapper<PagingData<MovieEntity>, PagingData<MovieDto>>
}