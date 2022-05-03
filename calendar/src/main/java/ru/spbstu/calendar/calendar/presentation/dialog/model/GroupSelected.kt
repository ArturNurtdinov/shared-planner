package ru.spbstu.calendar.calendar.presentation.dialog.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.spbstu.calendar.domain.model.Group

@Parcelize
data class GroupSelected(val group: Group, val isSelected: Boolean) : Parcelable {
    val id: Long
        get() = group.id
}