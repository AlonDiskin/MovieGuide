package com.diskin.alon.movieguide.reviews.di.moviessearchcontainer

import androidx.paging.PagingData
import com.diskin.alon.movieguide.common.appservices.UseCase
import com.diskin.alon.movieguide.common.presentation.Model
import com.diskin.alon.movieguide.common.presentation.ModelDispatcher
import com.diskin.alon.movieguide.common.presentation.ModelRequest
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.reviews.appservices.data.MovieDto
import com.diskin.alon.movieguide.reviews.appservices.usecase.SearchMoviesUseCase
import com.diskin.alon.movieguide.reviews.presentation.data.Movie
import com.diskin.alon.movieguide.reviews.presentation.data.SearchMoviesModelRequest
import com.diskin.alon.movieguide.reviews.presentation.util.MoviesPagingMapper
import com.diskin.alon.movieguide.reviews.presentation.util.MoviesSearchModelDispatcher
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import io.reactivex.Observable

@Module
@InstallIn(ViewModelComponent::class)
abstract class MoviesSearchModule {

    companion object {

        @ViewModelScoped
        @MoviesSearchModelDispatcher
        @Provides
        fun provideModelDispatcher(
            searchMoviesUseCase: SearchMoviesUseCase,
            moviesMapper: Mapper<Observable<PagingData<MovieDto>>, Observable<PagingData<Movie>>>
        ): Model {
            val map = HashMap<Class<out ModelRequest<*, *>>,Pair<UseCase<*, *>, Mapper<*, *>?>>()

            map[SearchMoviesModelRequest::class.java] = Pair(searchMoviesUseCase,moviesMapper)

            return ModelDispatcher(map)
        }
    }

    @ViewModelScoped
    @Binds
    abstract fun bindMoviePagingMapper(mapper: MoviesPagingMapper): Mapper<Observable<PagingData<MovieDto>>, Observable<PagingData<Movie>>>
}