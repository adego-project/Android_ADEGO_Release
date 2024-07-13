package com.seogaemo.android_adego.view.plan.calendar

import android.content.Context
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.seogaemo.android_adego.R

class PastDateDecorator(context: Context) : DayViewDecorator {
    private val color = ContextCompat.getColor(context, R.color.gray40)
    private val today = CalendarDay.today()

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return day.isBefore(today)
    }

    override fun decorate(view: DayViewFacade) {
        view.setDaysDisabled(true)
        view.addSpan(ForegroundColorSpan(color))
    }
}
