package com.diskin.alon.movieguide.news.di.articleconatiner

import androidx.lifecycle.ViewModelProvider
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.appservices.UseCase
import com.diskin.alon.movieguide.common.presentation.Model
import com.diskin.alon.movieguide.common.presentation.ModelDispatcher
import com.diskin.alon.movieguide.common.presentation.ModelRequest
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.news.appservices.data.ArticleDto
import com.diskin.alon.movieguide.news.appservices.usecase.BookmarkArticleUseCase
import com.diskin.alon.movieguide.news.appservices.usecase.GetArticleUseCase
import com.diskin.alon.movieguide.news.appservices.usecase.UnBookmarkArticlesUseCase
import com.diskin.alon.movieguide.news.presentation.controller.ArticleActivity
import com.diskin.alon.movieguide.news.presentation.data.Article
import com.diskin.alon.movieguide.news.presentation.data.ArticleModelRequest
import com.diskin.alon.movieguide.news.presentation.data.BookmarkingModelRequest
import com.diskin.alon.movieguide.news.presentation.data.UnBookmarkingModelRequest
import com.diskin.alon.movieguide.news.presentation.util.ArticleMapper
import com.diskin.alon.movieguide.news.presentation.viewmodel.ArticleViewModel
import com.diskin.alon.movieguide.news.presentation.viewmodel.ArticleViewModelImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import io.reactivex.Observable

@Module
abstract class ArticleModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun provideModelDispatcher(
            getArticleUseCase: GetArticleUseCase,
            bookmarkArticleUseCase: BookmarkArticleUseCase,
            unBookmarkArticlesUseCase: UnBookmarkArticlesUseCase,
            articleMapper: Mapper<Observable<Result<ArticleDto>>,Observable<Result<Article>>>
        ): Model {
            val map = HashMap<Class<out ModelRequest<*, *>>,Pair<UseCase<*, *>, Mapper<*, *>?>>()
            map[ArticleModelRequest::class.java] = Pair(getArticleUseCase,articleMapper)
            map[BookmarkingModelRequest::class.java] = Pair(bookmarkArticleUseCase,null)
            map[UnBookmarkingModelRequest::class.java] = Pair(unBookmarkArticlesUseCase,null)

            return ModelDispatcher(map)
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
    }

    @Binds
    abstract fun bindArticleMapper(mapper: ArticleMapper): Mapper<Observable<Result<ArticleDto>>,Observable<Result<Article>>>
}