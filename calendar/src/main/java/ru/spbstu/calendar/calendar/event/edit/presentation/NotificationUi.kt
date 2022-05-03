package ru.spbstu.calendar.calendar.event.edit.presentation

import ru.spbstu.calendar.domain.model.Notification

sealed class NotificationUi {
    abstract val viewType: Int

    data class NotificationUiItem(val notification: Notification) : NotificationUi() {
        override val viewType: Int = NOTIFICATION_UI_ITEM_VIEW_TYPE
    }

    object AddNotification : NotificationUi() {
        override val viewType: Int = ADD_NOTIFICATION_VIEW_TYPE
    }

    companion object {
        const val NOTIFICATION_UI_ITEM_VIEW_TYPE = 8319
        const val ADD_NOTIFICATION_VIEW_TYPE = 1028
    }
}