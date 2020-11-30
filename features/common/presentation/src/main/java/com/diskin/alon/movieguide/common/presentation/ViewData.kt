package com.diskin.alon.movieguide.common.presentation

/**
 * Different states in which ui related data can be,and its corresponding classes.
 */
sealed class ViewData<T>(val data: T?) {

    class Updating<T>(data: T? = null) : ViewData<T>(data)

    class Updated<T>(data: T? = null) : ViewData<T>(data)

    class Data<T>(data: T): ViewData<T>(data)

    /**
     * Error state.
     */
    class Error<T>(val error: ErrorViewData, data: T? = null): ViewData<T>(data)
}
