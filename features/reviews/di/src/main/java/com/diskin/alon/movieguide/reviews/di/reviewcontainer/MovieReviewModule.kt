package com.diskin.alon.movieguide.reviews.di.reviewcontainer

import android.content.Context
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.appservices.UseCase
import com.diskin.alon.movieguide.common.presentation.Model
import com.diskin.alon.movieguide.common.presentation.ModelDispatcher
import com.diskin.alon.movieguide.common.presentation.ModelRequest
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.reviews.appservices.data.MovieReviewDto
import com.diskin.alon.movieguide.reviews.appservices.usecase.AddMovieToFavoritesUseCase
import com.diskin.alon.movieguide.reviews.appservices.usecase.GetMovieReviewUseCase
import com.diskin.alon.movieguide.reviews.appservices.usecase.RemoveMovieFromFavoritesUseCase
import com.diskin.alon.movieguide.reviews.presentation.data.FavoriteMovieModelRequest
import com.diskin.alon.movieguide.reviews.presentation.data.MovieReview
import com.diskin.alon.movieguide.reviews.presentation.data.ReviewModelRequest
import com.diskin.alon.movieguide.reviews.presentation.data.UnFavoriteMovieModelRequest
import com.diskin.alon.movieguide.reviews.presentation.util.MovieReviewMapper
import com.diskin.alon.movieguide.reviews.presentation.util.MovieReviewModelDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import io.reactivex.Observable

@Module
@InstallIn(ViewModelComponent::class)
object MovieReviewModule {

    @ViewModelScoped
    @Provides
    fun bindMovieReviewMapper(@ApplicationContext context: Context): Mapper<Observable<Result<MovieReviewDto>>, Observable<Result<MovieReview>>> {
        return MovieReviewMapper(context.resources)
    }

    @ViewModelScoped
    @MovieReviewModelDispatcher
    @Provides
    fun provideModelDispatcher(
        getMovieReviewUseCase: GetMovieReviewUseCase,
        addMovieToFavoritesUseCase: AddMovieToFavoritesUseCase,
        removeMovieFromFavoritesUseCase: RemoveMovieFromFavoritesUseCase,
        reviewMapper: Mapper<Observable<Result<MovieReviewDto>>, Observable<Result<MovieReview>>>
    ): Model {
        val map = HashMap<Class<out ModelRequest<*, *>>,Pair<UseCase<*, *>, Mapper<*, *>?>>()

        map[ReviewModelRequest::class.java] = Pair(getMovieReviewUseCase,reviewMapper)
        map[FavoriteMovieModelRequest::class.java] = Pair(addMovieToFavoritesUseCase,null)
        map[UnFavoriteMovieModelRequest::class.java] = Pair(removeMovieFromFavoritesUseCase,null)

        return ModelDispatcher(map)
    }
}