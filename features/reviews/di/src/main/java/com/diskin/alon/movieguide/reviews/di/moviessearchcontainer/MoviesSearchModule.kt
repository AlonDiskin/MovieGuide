package com.diskin.alon.movieguide.reviews.di.moviessearchcontainer

import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagingData
import com.diskin.alon.movieguide.common.appservices.UseCase
import com.diskin.alon.movieguide.common.presentation.Model
import com.diskin.alon.movieguide.common.presentation.ModelDispatcher
import com.diskin.alon.movieguide.common.presentation.ModelRequest
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.reviews.appservices.data.MovieDto
import com.diskin.alon.movieguide.reviews.appservices.usecase.MovieDtoPagingMapper
import com.diskin.alon.movieguide.reviews.appservices.usecase.SearchMoviesUseCase
import com.diskin.alon.movieguide.reviews.domain.entities.MovieEntity
import com.diskin.alon.movieguide.reviews.presentation.controller.MoviesSearchFragment
import com.diskin.alon.movieguide.reviews.presentation.data.Movie
import com.diskin.alon.movieguide.reviews.presentation.data.SearchMoviesModelRequest
import com.diskin.alon.movieguide.reviews.presentation.util.MoviesPagingMapper
import com.diskin.alon.movieguide.reviews.presentation.viewmodel.MoviesSearchViewModel
import com.diskin.alon.movieguide.reviews.presentation.viewmodel.MoviesSearchViewModelImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import io.reactivex.Observable

@Module
abstract class MoviesSearchModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun provideModelDispatcher(
            searchMoviesUseCase: SearchMoviesUseCase,
            moviesMapper: Mapper<Observable<PagingData<MovieDto>>, Observable<PagingData<Movie>>>
        ): Model {
            val map = HashMap<Class<out ModelRequest<*, *>>,Pair<UseCase<*, *>, Mapper<*, *>?>>()

            map[SearchMoviesModelRequest::class.java] = Pair(searchMoviesUseCase,moviesMapper)

            return ModelDispatcher(map)
        }

        @JvmStatic
        @Provides
        fun provideMoviesSearchViewModel(
            fragment: MoviesSearchFragment,
            factory: MoviesSearchViewModelFactory
        ): MoviesSearchViewModel {
            return ViewModelProvider(fragment, factory)
                .get(MoviesSearchViewModelImpl::class.java)
        }
    }

    @Binds
    abstract fun bindMoviePagingMapper(mapper: MoviesPagingMapper): Mapper<Observable<PagingData<MovieDto>>, Observable<PagingData<Movie>>>

    @Binds
    abstract fun provideMoviePagingDtoMapper(mapper: MovieDtoPagingMapper): Mapper<PagingData<MovieEntity>, PagingData<MovieDto>>
}