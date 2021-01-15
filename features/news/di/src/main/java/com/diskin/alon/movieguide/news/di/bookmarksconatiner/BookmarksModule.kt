package com.diskin.alon.movieguide.news.di.bookmarksconatiner

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.appservices.UseCase
import com.diskin.alon.movieguide.common.presentation.Model
import com.diskin.alon.movieguide.common.presentation.ModelDispatcher
import com.diskin.alon.movieguide.common.presentation.ModelRequest
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.news.appservices.data.HeadlineDto
import com.diskin.alon.movieguide.news.appservices.usecase.GetBookmarkedHeadlinesUseCase
import com.diskin.alon.movieguide.news.appservices.usecase.UnBookmarkArticlesUseCase
import com.diskin.alon.movieguide.news.appservices.util.HeadlinesDtoMapper
import com.diskin.alon.movieguide.news.domain.ArticleEntity
import com.diskin.alon.movieguide.news.presentation.data.BookmarksModelRequest
import com.diskin.alon.movieguide.news.presentation.data.Headline
import com.diskin.alon.movieguide.news.presentation.data.UnBookmarkingModelRequest
import com.diskin.alon.movieguide.news.presentation.util.BookmarksModelDispatcher
import com.diskin.alon.movieguide.news.presentation.util.BookmarksModelRequestMapper
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import io.reactivex.Observable

@Module
@InstallIn(ViewModelComponent::class)
object BookmarksModule {

    @Provides
    fun provideBookmarksModelRequestMapper(mapper: Mapper<HeadlineDto,Headline>): Mapper<Observable<Result<List<HeadlineDto>>>,Observable<Result<List<Headline>>>> {
        return BookmarksModelRequestMapper(mapper)
    }

    @Provides
    fun provideHeadlinesDtoMapper(): Mapper<List<ArticleEntity>, List<HeadlineDto>> {
        return HeadlinesDtoMapper()
    }

    @ViewModelScoped
    @BookmarksModelDispatcher
    @Provides
    fun provideModelDispatcherMap(
        getBookmarkedHeadlinesUseCase: GetBookmarkedHeadlinesUseCase,
        unBookmarkArticlesUseCase: UnBookmarkArticlesUseCase,
        bookmarksMapper: Mapper<Observable<Result<List<HeadlineDto>>>, Observable<Result<List<Headline>>>>
    ): Model {
        val map = HashMap<Class<out ModelRequest<*, *>>,Pair<UseCase<*, *>, Mapper<*, *>?>>()
        map[BookmarksModelRequest::class.java] = Pair(getBookmarkedHeadlinesUseCase,bookmarksMapper)
        map[UnBookmarkingModelRequest::class.java] = Pair(unBookmarkArticlesUseCase,null)

        return ModelDispatcher(map)
    }
}