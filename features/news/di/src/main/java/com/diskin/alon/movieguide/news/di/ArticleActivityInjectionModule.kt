package com.diskin.alon.movieguide.news.di

import com.diskin.alon.movieguide.news.presentation.controller.ArticleActivity
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Module(subcomponents = [ArticleActivitySubcomponent::class])
abstract class ArticleActivityInjectionModule {

    @Binds
    @IntoMap
    @ClassKey(ArticleActivity::class)
    abstract fun bindAndroidInjectorFactory(factory: ArticleActivitySubcomponent.Factory): AndroidInjector.Factory<*>
}