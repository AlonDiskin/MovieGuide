package com.diskin.alon.movieguide.di

import com.diskin.alon.movieguide.news.di.common.NewsAppModule
import dagger.Module

@Module(includes = [NewsAppModule::class])
object AppModule