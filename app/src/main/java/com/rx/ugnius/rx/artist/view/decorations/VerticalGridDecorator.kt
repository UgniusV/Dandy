package com.rx.ugnius.rx.artist.view.decorations

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

class VerticalGridDecorator(context: Context, spacing: Int, private val spanCount: Int) : RecyclerView.ItemDecoration() {

    private var isMostLeft = true
    private val halfSpacing = spacing / 2

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val itemPosition = (view.layoutParams as RecyclerView.LayoutParams).viewAdapterPosition + 1
        if (isMostLeft) {
            isMostLeft = false
            outRect.right = halfSpacing
        } else if (itemPosition % spanCount == 0) {
            isMostLeft = true
            outRect.left = halfSpacing
        } else {
            outRect.right = halfSpacing
            outRect.left = halfSpacing
        }

    }
}