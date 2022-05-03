package ru.spbstu.calendar.calendar.presentation

import android.view.View
import com.kizitonwose.calendarview.ui.ViewContainer
import ru.spbstu.calendar.databinding.CalendarDayLayoutBinding

class DayViewContainer(view: View) : ViewContainer(view) {
    private val binding = CalendarDayLayoutBinding.bind(view)
    val dayText = binding.calendarDayLayoutDay
    val root = binding.calendarDayLayoutRoot
}