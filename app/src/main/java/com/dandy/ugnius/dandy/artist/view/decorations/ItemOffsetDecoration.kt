package com.dandy.ugnius.dandy.artist.view.decorations

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View
import com.dandy.ugnius.dandy.dpToPx

class ItemOffsetDecoration(context: Context, offset: Int) : RecyclerView.ItemDecoration() {

    private val offset = dpToPx(context, offset)

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.set(offset, offset, offset, offset * 3)
    }
}