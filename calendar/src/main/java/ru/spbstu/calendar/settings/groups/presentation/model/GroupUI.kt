package ru.spbstu.calendar.settings.groups.presentation.model

import ru.spbstu.calendar.domain.model.Group

sealed class GroupUi {
    abstract val viewType: Int

    data class GroupUiItem(val group: Group) : GroupUi() {
        override val viewType: Int = GROUP_UI_ITEM_VIEW_TYPE
    }

    object CreateGroupItem : GroupUi() {
        override val viewType: Int = CREATE_GROUP_VIEW_TYPE
    }

    companion object {
        const val GROUP_UI_ITEM_VIEW_TYPE = 1823
        const val CREATE_GROUP_VIEW_TYPE = 3219
    }
}