package com.diskin.alon.movieguide.common.presentation

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class CustomItemDecoration(private val span: Int, private val space: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        val doubleSpace = space

        outRect.bottom = space / 2
        outRect.top = space / 2
        outRect.left = space / 2
        outRect.right = space / 2

        if (isInFirstColumn(position)) {
            outRect.left = doubleSpace
        }

        if (isInLastColumn(position)) {
            outRect.right = doubleSpace
        }

        if (isInFirstRow(position)) {
            outRect.top = doubleSpace
        }

        if (isInLastRow(position,parent.adapter!!.itemCount)) {
            outRect.bottom = doubleSpace
        }
    }

    private fun isInLastRow(position: Int, count: Int): Boolean {
        return position >= (count - span)
    }

    private fun isInFirstRow(position: Int): Boolean {
        return position < span
    }

    private fun isInLastColumn(position: Int): Boolean {
        return position % span == (span - 1)
    }

    private fun isInFirstColumn(position: Int): Boolean {
        return position % span == 0
    }
}