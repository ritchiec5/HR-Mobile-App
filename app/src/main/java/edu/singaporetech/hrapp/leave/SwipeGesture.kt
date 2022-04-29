package edu.singaporetech.hrapp.leave

import android.content.Context
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import edu.singaporetech.hrapp.R
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

/**
 * Swipe gesture class to initialize swiping on components
 * @param context
 * @return ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) Response upon swipe being triggered on item
 */
abstract class SwipeGesture(context: Context) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    val deleteColor = ContextCompat.getColor(context, R.color.deleteColor)
    val labelColor = ContextCompat.getColor(context, R.color.white)
    val deleteIcon = R.drawable.ic_baseline_cancel_24

    /**
     * Function that sets movement flags for the swipegesture
     * @param recyclerView
     * @param viewHolder
     * @param target
     * @return Boolean
     */
    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    /**
     * Draws the intended view upon triggering a swipe.
     * @param c
     * @param recyclerView
     * @param viewHolder
     * @param dX
     * @param dY
     * @param actionState
     * @param isCurrentlyActive
     */
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            .addBackgroundColor(deleteColor)
            .addSwipeLeftLabel("Cancel")
            .setSwipeLeftLabelColor(labelColor)
            .addSwipeLeftCornerRadius(3, 5f)
            .addSwipeLeftPadding(5, 1f, 1f, 1f)
            .addActionIcon(deleteIcon)
            .create()
            .decorate()

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}