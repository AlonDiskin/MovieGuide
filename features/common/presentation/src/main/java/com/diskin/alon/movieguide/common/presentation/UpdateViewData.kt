package com.diskin.alon.movieguide.common.presentation

sealed class UpdateViewData {

    object Update : UpdateViewData()

    object EndUpdate : UpdateViewData()

    object Refresh : UpdateViewData()
}