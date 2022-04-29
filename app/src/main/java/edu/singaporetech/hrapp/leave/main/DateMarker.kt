package edu.singaporetech.hrapp.leave.main

import android.content.Context
import android.graphics.Color
import android.text.style.ForegroundColorSpan
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import java.util.HashSet

/**
 * Class for marking dates on the calendar
 * @param context
 * @param drawable
 * @param calendarDays1
 * @return DayViewDecorator
 */
class EventDecorator(
    var context: Context,
    private val drawable: Int,
    calendarDays1: List<CalendarDay>?
) :
    DayViewDecorator {
    private val dates: HashSet<CalendarDay>

    /**
     * Function to check if dates should be marked on the calendar
     * @param day
     * @return Boolean
     */
    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dates.contains(day)
    }

    /**
     * Function to decorate dates on calendar
     * @param view
     */
    override fun decorate(view: DayViewFacade) {
        // apply drawable to dayView
        view.setSelectionDrawable(context.resources.getDrawable(drawable))
        // white text color
        view.addSpan(ForegroundColorSpan(Color.WHITE))
    }

    init {
        dates = HashSet(calendarDays1)
    }
}