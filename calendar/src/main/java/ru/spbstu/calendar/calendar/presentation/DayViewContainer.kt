package ru.spbstu.calendar.calendar.presentation

import android.view.View
import com.kizitonwose.calendarview.ui.ViewContainer
import ru.spbstu.calendar.databinding.CalendarDayLayoutBinding

class DayViewContainer(view: View) : ViewContainer(view) {
    private val binding = CalendarDayLayoutBinding.bind(view)
    val dayText = binding.calendarDayLayoutDay
    val root = binding.calendarDayLayoutRoot
    val circle1 = binding.calendarDayLayoutCircle1
    val circle2 = binding.calendarDayLayoutCircle2
    val circle3 = binding.calendarDayLayoutCircle3
    val circle4 = binding.calendarDayLayoutCircle4
    val circle5 = binding.calendarDayLayoutCircle5
    val circle6 = binding.calendarDayLayoutCircle6
    val circle7 = binding.calendarDayLayoutCircle7
    val circle8 = binding.calendarDayLayoutCircle8
    val circle9 = binding.calendarDayLayoutCircle9

    val circles: List<View>
        get() = listOf(
            circle1,
            circle2,
            circle3,
            circle4,
            circle5,
            circle6,
            circle7,
            circle8,
            circle9
        )
}