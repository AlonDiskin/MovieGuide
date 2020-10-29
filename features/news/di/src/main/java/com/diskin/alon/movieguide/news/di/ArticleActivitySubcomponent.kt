package com.diskin.alon.movieguide.news.di

import com.diskin.alon.movieguide.news.presentation.controller.ArticleActivity
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = [NewsFeatureModule::class])
interface ArticleActivitySubcomponent : AndroidInjector<ArticleActivity> {

    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<ArticleActivity>
}