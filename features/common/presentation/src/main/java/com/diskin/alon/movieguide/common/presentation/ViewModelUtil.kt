package com.diskin.alon.movieguide.common.presentation

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider

fun <T : Any> createActivityViewModel(
    component: ComponentActivity,
    factoryProducer: (() -> ViewModelProvider.Factory)? = null
): Lazy<T> {
    return component.viewModels(factoryProducer)
}

fun <T : Any> createFragmentViewModel(
    fragment: Fragment,
    factoryProducer: (() -> ViewModelProvider.Factory)? = null
): Lazy<T> {
    return fragment.viewModels(factoryProducer = factoryProducer)
}