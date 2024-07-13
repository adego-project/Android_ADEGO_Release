package com.seogaemo.android_adego.view.alarm

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class EdgeItemDecoration(private val spanCount: Int, private val spacing: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val position: Int = parent.getChildAdapterPosition(view)

        if (position >= 0) {
            val column = position % spanCount
            outRect.apply {
                left = spacing - column * spacing / spanCount
                right = (column + 1) * spacing / spanCount
                if (position < spanCount) top = spacing
                bottom = spacing
            }
        } else {
            outRect.apply {
                left = 0
                right = 0
                top = 0
                bottom = 0
            }
        }
    }
}
