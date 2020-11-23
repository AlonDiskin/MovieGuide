package com.diskin.alon.movieguide.news.di.feature

import dagger.Module

@Module(includes = [
    NewsPresentationModule::class,
    NewsAppServiceModule::class,
    NewsDataModule::class
])
abstract class NewsFeatureModule