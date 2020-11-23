package com.diskin.alon.movieguide.news.di.entryinjection

import com.diskin.alon.movieguide.news.di.subcomponent.ArticleActivitySubcomponent
import com.diskin.alon.movieguide.news.di.subcomponent.BookmarksFragmentSubcomponent
import com.diskin.alon.movieguide.news.di.subcomponent.MoviesHeadlinesFragmentSubcomponent
import com.diskin.alon.movieguide.news.presentation.controller.ArticleActivity
import com.diskin.alon.movieguide.news.presentation.controller.BookmarksFragment
import com.diskin.alon.movieguide.news.presentation.controller.HeadlinesFragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Module(subcomponents = [
    ArticleActivitySubcomponent::class,
    BookmarksFragmentSubcomponent::class,
    MoviesHeadlinesFragmentSubcomponent::class
])
abstract class NewsInjectionModule {

    @Binds
    @IntoMap
    @ClassKey(ArticleActivity::class)
    abstract fun bindArticleActivityAndroidInjectorFactory(factory: ArticleActivitySubcomponent.Factory): AndroidInjector.Factory<*>

    @Binds
    @IntoMap
    @ClassKey(HeadlinesFragment::class)
    abstract fun bindMoviesHeadlinesFragmentAndroidInjectorFactory(factory: MoviesHeadlinesFragmentSubcomponent.Factory): AndroidInjector.Factory<*>

    @Binds
    @IntoMap
    @ClassKey(BookmarksFragment::class)
    abstract fun bindBookmarksFragmentAndroidInjectorFactory(factory: BookmarksFragmentSubcomponent.Factory): AndroidInjector.Factory<*>
}