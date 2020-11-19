package com.diskin.alon.movieguide.di

import android.app.Application
import com.diskin.alon.movieguide.MovieGuideApp
import com.diskin.alon.movieguide.home.di.MainActivityInjectionModule
import com.diskin.alon.movieguide.news.di.ArticleActivityInjectionModule
import com.diskin.alon.movieguide.news.di.MoviesHeadlinesFragmentInjectionModule
import com.diskin.alon.movieguide.news.di.NewsNetworkingModule
import com.diskin.alon.movieguide.reviews.di.MovieReviewActivityInjectionModule
import com.diskin.alon.movieguide.reviews.di.MoviesFragmentInjectionModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidInjectionModule::class,
    AndroidSupportInjectionModule::class,
    NavigationModule::class,
    NetworkingModule::class,
    AppModule::class,
    MainActivityInjectionModule::class,
    MoviesHeadlinesFragmentInjectionModule::class,
    ArticleActivityInjectionModule::class,
    MoviesFragmentInjectionModule::class,
    MovieReviewActivityInjectionModule::class
])
interface AppComponent : AndroidInjector<MovieGuideApp> {

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance app: Application): AppComponent
    }
}