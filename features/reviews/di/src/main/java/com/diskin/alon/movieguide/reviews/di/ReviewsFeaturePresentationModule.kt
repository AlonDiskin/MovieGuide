package com.diskin.alon.movieguide.reviews.di

import android.app.Application
import android.content.res.Resources
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagingData
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.appservices.UseCase
import com.diskin.alon.movieguide.common.presentation.Model
import com.diskin.alon.movieguide.common.presentation.ModelDispatcher
import com.diskin.alon.movieguide.common.presentation.ModelRequest
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.reviews.appservices.data.MovieDto
import com.diskin.alon.movieguide.reviews.appservices.data.MovieReviewDto
import com.diskin.alon.movieguide.reviews.appservices.usecase.GetMovieReviewUseCase
import com.diskin.alon.movieguide.reviews.presentation.controller.MovieReviewActivity
import com.diskin.alon.movieguide.reviews.presentation.controller.MoviesFragment
import com.diskin.alon.movieguide.reviews.presentation.data.Movie
import com.diskin.alon.movieguide.reviews.presentation.data.MovieReview
import com.diskin.alon.movieguide.reviews.presentation.data.ReviewModelRequest
import com.diskin.alon.movieguide.reviews.presentation.util.MoviePagingMapper
import com.diskin.alon.movieguide.reviews.presentation.util.MovieReviewMapper
import com.diskin.alon.movieguide.reviews.presentation.viewmodel.MovieReviewViewModel
import com.diskin.alon.movieguide.reviews.presentation.viewmodel.MovieReviewViewModelImpl
import com.diskin.alon.movieguide.reviews.presentation.viewmodel.MoviesViewModel
import com.diskin.alon.movieguide.reviews.presentation.viewmodel.MoviesViewModelImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import io.reactivex.Observable

@Module
abstract class ReviewsFeaturePresentationModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun bindMovieReviewMapper(app: Application): Mapper<Observable<Result<MovieReviewDto>>, Observable<Result<MovieReview>>> {
            return MovieReviewMapper(app.resources)
        }

        @JvmStatic
        @Provides
        fun provideModelDispatcher(
            getMovieReviewUseCase: GetMovieReviewUseCase,
            reviewMapper: Mapper<Observable<Result<MovieReviewDto>>, Observable<Result<MovieReview>>>
        ): Model {
            val map = HashMap<Class<out ModelRequest<*, *>>,Pair<UseCase<*, *>, Mapper<*, *>?>>()

            map[ReviewModelRequest::class.java] = Pair(getMovieReviewUseCase,reviewMapper)

            return ModelDispatcher(map)
        }

        @JvmStatic
        @Provides
        fun provideMoviesViewModel(
            fragment: MoviesFragment,
            factory: MoviesViewModelFactory
        ): MoviesViewModel {
            return ViewModelProvider(fragment, factory)
                .get(MoviesViewModelImpl::class.java)
        }

        @JvmStatic
        @Provides
        fun provideMovieReviewViewModel(
            activity: MovieReviewActivity,
            factory: MovieReviewViewModelFactory
        ): MovieReviewViewModel {
            return ViewModelProvider(activity, factory)
                .get(MovieReviewViewModelImpl::class.java)
        }
    }

    @Binds
    abstract fun bindMoviePagingMapper(mapper: MoviePagingMapper): Mapper<PagingData<MovieDto>, PagingData<Movie>>
}