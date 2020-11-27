package com.diskin.alon.movieguide.news.di.articleconatiner

import com.diskin.alon.movieguide.news.presentation.controller.ArticleActivity
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = [ArticleModule::class])
interface ArticleActivitySubComponent : AndroidInjector<ArticleActivity> {

    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<ArticleActivity>
}