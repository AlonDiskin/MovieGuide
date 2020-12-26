package com.diskin.alon.movieguide.news.di.common

import com.diskin.alon.movieguide.news.di.articleconatiner.ArticleActivitySubComponent
import com.diskin.alon.movieguide.news.di.bookmarksconatiner.BookmarksFragmentSubComponent
import com.diskin.alon.movieguide.news.di.headlinescontainer.HeadlinesFragmentSubComponent
import com.diskin.alon.movieguide.news.presentation.controller.ArticleActivity
import com.diskin.alon.movieguide.news.presentation.controller.BookmarksFragment
import com.diskin.alon.movieguide.news.presentation.controller.HeadlinesFragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Module(subcomponents = [
    ArticleActivitySubComponent::class,
    BookmarksFragmentSubComponent::class,
    HeadlinesFragmentSubComponent::class
])
abstract class NewsInjectionModule {

    @Binds
    @IntoMap
    @ClassKey(ArticleActivity::class)
    abstract fun bindArticleActivityAndroidInjectorFactory(factory: ArticleActivitySubComponent.Factory): AndroidInjector.Factory<*>

    @Binds
    @IntoMap
    @ClassKey(HeadlinesFragment::class)
    abstract fun bindMoviesHeadlinesFragmentAndroidInjectorFactory(factory: HeadlinesFragmentSubComponent.Factory): AndroidInjector.Factory<*>

    @Binds
    @IntoMap
    @ClassKey(BookmarksFragment::class)
    abstract fun bindBookmarksFragmentAndroidInjectorFactory(factory: BookmarksFragmentSubComponent.Factory): AndroidInjector.Factory<*>
}