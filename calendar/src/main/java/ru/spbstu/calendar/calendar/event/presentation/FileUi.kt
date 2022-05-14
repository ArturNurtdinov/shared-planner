package ru.spbstu.calendar.calendar.event.presentation

sealed class FileUi {
    abstract val viewType: Int

    data class FileUiItem(val name: String) : FileUi() {
        override val viewType: Int = FILE_UI_ITEM_VIEW_TYPE
    }

    object AddFileItem : FileUi() {
        override val viewType: Int = ADD_FILE_VIEW_TYPE
    }

    companion object {
        const val FILE_UI_ITEM_VIEW_TYPE = 9213
        const val ADD_FILE_VIEW_TYPE = 1229
    }
}