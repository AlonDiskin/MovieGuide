package com.diskin.alon.movieguide.news.di.bookmarksconatiner

import androidx.lifecycle.ViewModelProvider
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.appservices.UseCase
import com.diskin.alon.movieguide.common.presentation.Model
import com.diskin.alon.movieguide.common.presentation.ModelDispatcher
import com.diskin.alon.movieguide.common.presentation.ModelRequest
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.news.appservices.data.HeadlineDto
import com.diskin.alon.movieguide.news.appservices.usecase.GetBookmarkedHeadlinesUseCase
import com.diskin.alon.movieguide.news.appservices.util.HeadlinesMapper
import com.diskin.alon.movieguide.news.domain.ArticleEntity
import com.diskin.alon.movieguide.news.presentation.controller.BookmarksFragment
import com.diskin.alon.movieguide.news.presentation.data.BookmarksModelRequest
import com.diskin.alon.movieguide.news.presentation.data.Headline
import com.diskin.alon.movieguide.news.presentation.util.BookmarksModelRequestMapper
import com.diskin.alon.movieguide.news.presentation.util.HeadlineMapper
import com.diskin.alon.movieguide.news.presentation.viewmodel.BookmarksViewModel
import com.diskin.alon.movieguide.news.presentation.viewmodel.BookmarksViewModelImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import io.reactivex.Observable

@Module
abstract class BookmarksModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun provideHeadlinesDtoMapper(): Mapper<Result<List<ArticleEntity>>, Result<List<HeadlineDto>>> {
            return HeadlinesMapper()
        }

        @JvmStatic
        @Provides
        fun provideHeadlinesMapper(
            mapper: Mapper<HeadlineDto,Headline>
        ): Mapper<Observable<Result<List<HeadlineDto>>>,Observable<Result<List<Headline>>>> {
            return BookmarksModelRequestMapper(mapper)
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

        @JvmStatic
        @Provides
        fun provideBookmarksViewModel(
            fragment: BookmarksFragment,
            factory: BookmarksViewModelFactory
        ): BookmarksViewModel {
            return ViewModelProvider(fragment, factory)
                .get(BookmarksViewModelImpl::class.java)
        }
    }

    @Binds
    abstract fun bindsHeadlineMapper(mapper: HeadlineMapper): Mapper<HeadlineDto,Headline>
}