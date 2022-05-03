package ru.spbstu.calendar.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Group(
    val id: Long,
    val title: String,
    val notificationsEnabled: Boolean,
    val participants: Int,
): Parcelable