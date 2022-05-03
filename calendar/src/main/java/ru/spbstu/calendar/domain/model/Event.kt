package ru.spbstu.calendar.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Event(
    val id: Long,
    val startTime: Long, val endTime: Long?, val title: String, val group: Group
) : Parcelable