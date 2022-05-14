package ru.spbstu.common.domain

enum class EventTypes(val value: Int) {
    EVENT(0),
    NOTIFICATION(1);

    companion object {
        fun fromInt(value: Int): EventTypes = EventTypes.values().first { it.value == value }
    }
}