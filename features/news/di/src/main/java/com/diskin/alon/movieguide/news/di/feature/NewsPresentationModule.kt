package com.diskin.alon.movieguide.news.di.feature

import androidx.lifecycle.ViewModelProvider
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.appservices.UseCase
import com.diskin.alon.movieguide.common.presentation.Model
import com.diskin.alon.movieguide.common.presentation.ModelDispatcher
import com.diskin.alon.movieguide.common.presentation.ModelRequest
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.news.appservices.data.HeadlineDto
import com.diskin.alon.movieguide.news.appservices.usecase.GetBookmarkedHeadlinesUseCase
import com.diskin.alon.movieguide.news.presentation.controller.ArticleActivity
import com.diskin.alon.movieguide.news.presentation.controller.BookmarksFragment
import com.diskin.alon.movieguide.news.presentation.controller.HeadlinesFragment
import com.diskin.alon.movieguide.news.presentation.data.BookmarksModelRequest
import com.diskin.alon.movieguide.news.presentation.data.Headline
import com.diskin.alon.movieguide.news.presentation.util.BookmarksModelRequestMapper
import com.diskin.alon.movieguide.news.presentation.util.HeadlineMapper
import com.diskin.alon.movieguide.news.presentation.viewmodel.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import io.reactivex.Observable

@Module
abstract class NewsPresentationModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun provideBookmarksMapper(
            mapper: Mapper<HeadlineDto,Headline>
        ): Mapper<Observable<Result<List<HeadlineDto>>>,Observable<Result<List<Headline>>>> {
            return BookmarksModelRequestMapper(mapper)
        }

        @JvmStatic
        @Provides
        fun provideMoviesHeadlinesViewModel(
            fragment: HeadlinesFragment,
            factory: MoviesHeadlinesViewModelProvider
        ): HeadlinesViewModel {
            return ViewModelProvider(fragment, factory)
                .get(HeadlinesViewModelImpl::class.java)
        }

        @JvmStatic
        @Provides
        fun provideArticleViewModel(
            activity: ArticleActivity,
            factory: ArticleViewModelFactory
        ): ArticleViewModel {
            return ViewModelProvider(activity, factory)
                .get(ArticleViewModelImpl::class.java)
        }

        @JvmStatic
        @Provides
        fun provideBookmarksViewModel(
            fragment: BookmarksFragment,
            factory: BookmarksViewModelFactory
        ): BookmarksViewModel {
            return ViewModelProvider(fragment, factory)
                .get(BookmarksViewModelImpl::class.java)
        }

        @JvmStatic
        @Provides
        fun provideModelDispatcher(
            getBookmarkedHeadlinesUseCase: GetBookmarkedHeadlinesUseCase,
            bookmarksMapper: Mapper<Observable<Result<List<HeadlineDto>>>, Observable<Result<List<Headline>>>>
        ): Model {
            val map = HashMap<Class<out ModelRequest<*, *>>,Pair<UseCase<*, *>, Mapper<*, *>?>>()
            map[BookmarksModelRequest::class.java] = Pair(getBookmarkedHeadlinesUseCase,bookmarksMapper)

            return ModelDispatcher(map)
        }
    }

    @Binds
    abstract fun bindNewsHeadlineMapper(mapper: HeadlineMapper): Mapper<HeadlineDto,Headline>
}