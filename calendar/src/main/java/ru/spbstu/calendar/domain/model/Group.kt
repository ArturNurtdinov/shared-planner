package ru.spbstu.calendar.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Group(
    val id: Long,
    val name: String,
    val color: Int,
    val notificationsEnabled: Boolean,
    val participants: Int,
): Parcelable