package com.diskin.alon.movieguide.reviews.di

import dagger.Module

@Module(includes = [
    ReviewsFeaturePresentationModule::class,
    ReviewsFeatureAppServicesModule::class,
    ReviewsFeatureDataModule::class
])
abstract class ReviewsFeatureModule