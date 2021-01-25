package com.diskin.alon.movieguide.common.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView

fun setCustomDecoration(context: Context,recyclerView: RecyclerView, span: Int) {
    recyclerView.addItemDecoration(
        CustomItemDecoration(
            span,
            context.resources.getDimensionPixelSize(R.dimen.item_margin)
        )
    )
}